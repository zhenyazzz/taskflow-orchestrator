package org.example.notificationservice.client;

import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.grpc.user.UserServiceGrpc;
import org.example.grpc.user.GetUserByIdRequest;
import org.example.grpc.user.UserDto;
import org.example.notificationservice.dto.response.UserResponse;
import org.example.notificationservice.service.cache.UserCacheService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    @Qualifier("userServiceChannel")
    private final ManagedChannel userServiceChannel;
    private final UserCacheService userCacheService;

    private UserServiceGrpc.UserServiceBlockingStub getStub() {
        return UserServiceGrpc.newBlockingStub(userServiceChannel);
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
        return userIds.stream()
                .map(this::getUserById)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());
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