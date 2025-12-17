package org.example.notificationservice.client;

import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.grpc.user.UserServiceGrpc;
import org.example.grpc.user.GetUserByIdRequest;
import org.example.grpc.user.UserDto;
import org.example.grpc.user.GetUsersByIdsRequest;
import org.example.grpc.user.GetUsersByIdsResponse;
import org.example.notificationservice.dto.response.UserResponse;
import org.example.notificationservice.service.cache.UserCacheService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    @Qualifier("userServiceChannel")
    private final ManagedChannel userServiceChannel;
    private final UserCacheService userCacheService;

    private UserServiceGrpc.UserServiceBlockingStub getStub() {
        return UserServiceGrpc.newBlockingStub(userServiceChannel)
        .withDeadlineAfter(2, TimeUnit.SECONDS);
    }

    public UserResponse getUserById(String userId) {
        log.debug("Getting user by ID: {}", userId);
        
        UserResponse userResponse = userCacheService.getUserFromCache(userId);
        
        if (userResponse == null) {
            userResponse = getFromService(userId);
        }

        if (userResponse != null) {
            userCacheService.cacheUser(userId, userResponse);
        }

        return userResponse;
    }

    public List<UserResponse> getUsersByIds(Set<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }
    
        log.debug("Getting users by IDs (batch): {}", userIds);
    
        Map<String, UserResponse> cachedUsers = userCacheService.getUsersFromCache(userIds);
    
        Set<String> missingIds = userIds.stream()
                .filter(id -> !cachedUsers.containsKey(id))
                .collect(Collectors.toSet());
    
        if (!missingIds.isEmpty()) {
            List<UserResponse> fetchedUsers = getFromServiceBatch(missingIds);
    
            fetchedUsers.forEach(user ->
                    userCacheService.cacheUser(user.id(), user)
            );
    
            fetchedUsers.forEach(user ->
                    cachedUsers.put(user.id(), user)
            );
        }
    
        return userIds.stream()
                .map(cachedUsers::get)
                .filter(user -> user != null)
                .toList();
    }
    

    private List<UserResponse> getFromServiceBatch(Set<String> userIds) {
        UserServiceGrpc.UserServiceBlockingStub stub = getStub();
    
        GetUsersByIdsRequest request = GetUsersByIdsRequest.newBuilder()
                .addAllUserIds(userIds)
                .build();
    
        try {
            GetUsersByIdsResponse response = stub.getUsersByIds(request);
    
            return response.getUsersList().stream()
                    .map(this::convertToUserResponse)
                    .toList();
    
        } catch (Exception e) {
            log.error("Failed to get users {} from user-service (batch)", userIds, e);
            return List.of();
        }
    }
    

    private UserResponse getFromService(String userId) {
        UserServiceGrpc.UserServiceBlockingStub stub = getStub();
        GetUserByIdRequest request = GetUserByIdRequest.newBuilder()
                .setUserId(userId)
                .build();
        try {
            UserDto userDto = stub.getUserById(request);
            return convertToUserResponse(userDto);
        } catch (Exception e) {
            log.error("Failed to get user {} from user-service", userId, e);
            return null;
        }
    }

    private UserResponse convertToUserResponse(UserDto grpcDto) {
        return new UserResponse(
                grpcDto.getId(),
                grpcDto.getUsername(),
                grpcDto.getEmail(),
                grpcDto.getFirstName(),
                grpcDto.getLastName()
        );
    }

}