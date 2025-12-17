package org.example.userservice.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.grpc.user.UserServiceGrpc;
import org.example.grpc.user.GetUserByIdRequest;
import org.example.grpc.user.UserDto;
import org.example.grpc.user.GetUsersByIdsRequest;
import org.example.grpc.user.GetUsersByIdsResponse;
import org.example.userservice.model.User;
import org.example.userservice.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
            User user = userService.findUserById(uuid);
            
            UserDto grpcResponse = convertToGrpcResponse(user);
            
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

    @Override
    public void getUsersByIds(
            GetUsersByIdsRequest request,
            StreamObserver<GetUsersByIdsResponse> responseObserver
    ) {
        try {
            log.debug("gRPC: Getting users by IDs: {}", request.getUserIdsList());
            Set<String> ids = new HashSet<>(request.getUserIdsList());
        
            List<UserDto> users = userService.findAllByIds(ids).stream()
                    .map(this::convertToGrpcResponse)
                    .toList();
        
            GetUsersByIdsResponse response = GetUsersByIdsResponse.newBuilder()
                    .addAllUsers(users)
                    .build();
        
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            log.debug("gRPC: Successfully retrieved {} users", users.size());
        } catch (IllegalArgumentException e) {
            log.error("gRPC: Invalid UUID format in list: {}", request.getUserIdsList(), e);
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid user ID format in the list: " + e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("gRPC: Error getting users by IDs: {}", request.getUserIdsList(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error: " + e.getMessage())
                    .asRuntimeException());
        }
    }
        

    private UserDto convertToGrpcResponse(User user) {
        return UserDto.newBuilder()
                .setId(user.getId().toString())
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setFirstName(user.getFirstName() != null ? user.getFirstName() : "")
                .setLastName(user.getLastName() != null ? user.getLastName() : "")
                .build();
    }
}

