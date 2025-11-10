package org.example.userservice.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.grpc.user.UserServiceGrpc;
import org.example.grpc.user.GetUserByIdRequest;
import org.example.grpc.user.UserDto;
import org.example.userservice.dto.response.UserResponse;
import org.example.userservice.service.UserService;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final UserService userService;

    @Override
    public void getUserById(GetUserByIdRequest request, 
                           StreamObserver<UserDto> responseObserver) {
        try {
            String userId = request.getUserId();
            log.debug("gRPC: Getting user by ID: {}", userId);
            
            UUID uuid = UUID.fromString(userId);
            UserResponse userResponse = userService.getUserById(uuid);
            
            UserDto grpcResponse = convertToGrpcResponse(userResponse);
            
            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();
            log.debug("gRPC: Successfully retrieved user: {}", userId);
        } catch (IllegalArgumentException e) {
            log.error("gRPC: Invalid UUID format: {}", request.getUserId(), e);
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid user ID format: " + e.getMessage())
                    .asRuntimeException());
        } catch (org.example.userservice.exception.UserNotFoundException e) {
            log.error("gRPC: User not found: {}", request.getUserId(), e);
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("User not found: " + request.getUserId())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("gRPC: Error getting user: {}", request.getUserId(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    private UserDto convertToGrpcResponse(org.example.userservice.dto.response.UserResponse userResponse) {
        return UserDto.newBuilder()
                .setId(userResponse.id().toString())
                .setUsername(userResponse.username())
                .setEmail(userResponse.email())
                .setFirstName(userResponse.firstName() != null ? userResponse.firstName() : "")
                .setLastName(userResponse.lastName() != null ? userResponse.lastName() : "")
                .build();
    }
}

