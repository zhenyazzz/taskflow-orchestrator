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
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.UUID;

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

    public Mono<UserDto> getUserByIdAsync(String userId) {
        log.debug("Getting user by ID: {}", userId);
        
        return getFromCacheAsync(userId)
            .switchIfEmpty(Mono.defer(() -> getFromServiceAsync(userId)))
            .doOnNext(user -> log.debug("Retrieved user: {}", user.getId()))
            .doOnError(error -> log.error("Failed to get user {}: {}", userId, error.getMessage()));
    }

    private Mono<UserDto> getFromCacheAsync(String userId) {
        return userCacheService.getUserFromCache(userId)
            .map(this::convertToGrpcDto)
            .doOnNext(user -> log.debug("Retrieved user from cache: {}", user.getId()));
    }

    private Mono<UserDto> getFromServiceAsync(String userId) {
        log.debug("Fetching user {} from gRPC service", userId);
        
        return Mono.fromCallable(() -> {
                    UserServiceGrpc.UserServiceBlockingStub stub = getStub();
                    GetUserByIdRequest request = GetUserByIdRequest.newBuilder()
                            .setUserId(userId)
                            .build();
                    
                    return stub.getUserById(request);
                })
                .timeout(Duration.ofSeconds(10))
                .flatMap(user -> cacheUser(userId, user).thenReturn(user))
                .doOnSuccess(user -> log.debug("Fetched user via gRPC: {}", user.getId()))
                .doOnError(error -> log.error("Failed to fetch user {} via gRPC: {}", userId, error.getMessage()))
                .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic());
    }

    private Mono<Void> cacheUser(String userId, UserDto user) {
        UserResponse userResponse = convertToUserResponse(user);
        return userCacheService.cacheUser(userId, userResponse);
    }

    private UserResponse convertToUserResponse(UserDto grpcDto) {
        return new UserResponse(
                UUID.fromString(grpcDto.getId()),
                grpcDto.getUsername(),
                grpcDto.getEmail(),
                grpcDto.getFirstName(),
                grpcDto.getLastName()
        );
    }

    private UserDto convertToGrpcDto(UserResponse userResponse) {
        return UserDto.newBuilder()
                .setId(userResponse.id().toString())
                .setUsername(userResponse.username())
                .setEmail(userResponse.email())
                .setFirstName(userResponse.firstName() != null ? userResponse.firstName() : "")
                .setLastName(userResponse.lastName() != null ? userResponse.lastName() : "")
                .build();
    }
}