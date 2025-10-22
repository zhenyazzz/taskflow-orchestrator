package org.example.userservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.events.enums.UserStatus;
import org.example.events.user.UserRegistrationEvent;
import org.example.events.user.UserRoleUpdateEvent;
import org.example.userservice.dto.request.CreateUserRequest;
import org.example.userservice.dto.request.UpdateUserRequest;
import org.example.userservice.dto.response.ProfileResponse;
import org.example.userservice.dto.response.UserResponse;
import org.example.userservice.exception.UserAlreadyExistsException;
import org.example.userservice.exception.UserNotFoundException;

import org.example.userservice.kafka.producer.KafkaProducerService;

import org.example.userservice.mapper.UserMapper;
import org.example.userservice.model.User;
import org.example.userservice.repository.UserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;
    private final UserMapper userMapper;

    public User findUserById(UUID id) {
        log.info("Getting user with id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    public List<UserResponse> getAllUsers() {
        log.info("Getting all users list");
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    public Page<UserResponse> getUsersWithPagination(int page, int size, String sort, String username, String role, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.unsorted());
        
        String orderByClause = convertSortToOrderByClause(sort);

        Page<User> users = userRepository.findUsersWithFilters(username, role, status, orderByClause, pageable);
        return users.map(userMapper::toUserResponse);
    }

    private String convertSortToOrderByClause(String sort) {
        try {
            String[] sortParams = sort.split(",");
            String property = sortParams[0];
            String direction = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1])
                ? "DESC"
                : "ASC";
            
            if (!isValidSortProperty(property)) {
                log.warn("Invalid sort property provided: {}. Defaulting to username.", property);
                property = "username";
                direction = "ASC";
            }

            return String.format("%s %s", property, direction);
        } catch (Exception e) {
            log.warn("Error parsing sort parameter: {}. Defaulting to username ASC.", sort, e);
            return "username ASC";
        }
    }

    private boolean isValidSortProperty(String property) {
        return List.of("username", "email", "firstName", "lastName", "createdAt", "updatedAt", "status").contains(property);
    }

    public UserResponse getUserById(UUID id) {
        log.info("Getting user with id: {}", id);
        return userRepository.findById(id)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.email())) {
            throw new UserAlreadyExistsException("User already exists");
        }
        log.info("Creating user: {}", userRequest.username());
        User user = userMapper.toUser(userRequest);
        User savedUser = userRepository.save(user);
        
        kafkaProducerService.sendUserCreatedEvent(
            savedUser.getId(),
            userMapper.toUserCreatedEvent(savedUser,userRequest.password())
            );
        
        return userMapper.toUserResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest userRequest) {
        User existing = findUserById(id);
        
        log.info("Updating user with id {}: {}", id, userRequest);
        userMapper.updateUserFromRequest(userRequest, existing);
        User updated = userRepository.save(existing);
        
        kafkaProducerService.sendUserProfileUpdatedEvent(
            id,
            userMapper.toUserProfileUpdatedEvent(updated, userRequest.password())
        );
        
        return userMapper.toUserResponse(updated);
    }

    @Transactional
    public void deleteUser(UUID id) {
        userRepository.findById(id).ifPresent(user -> {
            log.info("Deleting user with id: {}", id);
            userRepository.deleteById(id);
            
            kafkaProducerService.sendUserDeletedEvent(
                id,
                userMapper.toUserDeletedEvent(user));
        });
    }

    public ProfileResponse getMyProfile(UUID id) {
        User user = findUserById(id);
        return userMapper.toProfileResponse(user);
    }

    @Transactional
    public ProfileResponse updateMyProfile(UUID id, UpdateUserRequest request) {
        User existing = findUserById(id);
        
        log.info("Updating user profile with id {}: {}", id, request);
        userMapper.updateUserFromRequest(request, existing);
        User updated = userRepository.save(existing);
        
        kafkaProducerService.sendUserProfileUpdatedEvent(
            id,
            userMapper.toUserProfileUpdatedEvent(updated, request.password())
        );
        
        return userMapper.toProfileResponse(updated);
    }

    @Transactional
    public void deleteMyProfile(UUID id) {
        User user = findUserById(id);
        userRepository.delete(user);
        kafkaProducerService.sendUserDeletedEvent(
            id,
            userMapper.toUserDeletedEvent(user)
        );
    }

    @Transactional
    public void handleUserRegistration(UserRegistrationEvent event) {
        if (userRepository.existsById(event.id())) {
            throw new UserAlreadyExistsException("User already exists");
        }
        log.info("Saving user {}", event);
        User user = userMapper.toUser(event);
        try {
            userRepository.insertWithCustomId(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    UserStatus.ACTIVE.name()
            );
            event.roles().forEach(role -> {
                userRepository.insertUserRole(user.getId(),role.name());
            });

            log.info("User created from event: {}", user.getId());

        } catch (DataAccessException e) {
            log.error("Failed to create user from event: {}", user.getId(), e);
        }
    }

    @Transactional
    public void handleUserRoleUpdate(UserRoleUpdateEvent event) {
        User user = findUserById(event.id());
        switch (event.action()) {
            case ADD -> user.getRoles().add(event.role());
            case REMOVE -> user.getRoles().remove(event.role());
            default -> throw new IllegalArgumentException("Unsupported role action: " + event.action());
        }
        userRepository.save(user);
    }
}