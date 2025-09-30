package org.example.authservice.service;

import java.util.HashSet;
import java.util.Set;

import org.example.authservice.dto.AssignRoleRequest;
import org.example.authservice.dto.JwtResponse;
import org.example.authservice.dto.LoginRequest;
import org.example.authservice.dto.RegisterRequest;
import org.example.authservice.dto.RemoveRoleRequest;
import org.example.authservice.exception.exceptions.AuthorizationHeaderException;
import org.example.authservice.exception.exceptions.InvalidCredentialsException;
import org.example.authservice.exception.exceptions.InvalidTokenException;
import org.example.authservice.exception.exceptions.UserAlreadyExistsException;
import org.example.authservice.exception.exceptions.UserNotFoundException;
import org.example.authservice.model.User;
import org.example.authservice.repository.UserRepository;
import org.example.authservice.util.JwtUtil;
import org.example.events.enums.Role;
import org.example.events.enums.RoleAction;
import org.example.events.user.LoginFailEvent;
import org.example.events.user.UserCreatedEvent;
import org.example.events.user.UserDeletedEvent;
import org.example.events.user.UserProfileUpdatedEvent;
import org.example.events.user.UserRoleUpdateEvent;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import java.util.UUID;


import org.example.authservice.kafka.producer.KafkaProducerService;
import org.example.authservice.mapper.UserMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Transactional
    public JwtResponse registerUser(RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.username()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }
    
        User user = userMapper.toUser(registerRequest, passwordEncoder);
        User savedUser = userRepository.save(user);

        log.info("Пользователь сохранен: {}", savedUser);

        kafkaProducerService.sendUserRegistrationEvent(
                savedUser.getId(),
                userMapper.toUserRegistrationEvent(
                        savedUser,
                        registerRequest.firstName(),
                        registerRequest.lastName(),
                        registerRequest.phone())
        );

        return userMapper.toJwtResponse(savedUser, jwtUtil);
    }

    public JwtResponse loginUser(LoginRequest loginRequest, String userAgent) {
        User user = null;
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()));
        
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            user = userRepository.findByUsername(loginRequest.username())
                    .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        
            kafkaProducerService.sendUserLoginEvent(
                    user.getId(),
                    userMapper.toUserLoginEvent(user,userAgent)
            );
        
            return userMapper.toJwtResponse(user, jwtUtil);
            
        } catch (BadCredentialsException | UserNotFoundException e) {
            kafkaProducerService.sendLoginFailEvent(
                    loginRequest.username(),
                    new LoginFailEvent(
                            user != null ? user.getId().toString() : null,
                            loginRequest.username(),
                            user != null ? user.getEmail() : null,
                            e.getClass().getSimpleName(),
                            userAgent
                    )
            );
            
            if (e instanceof BadCredentialsException) {
                throw new InvalidCredentialsException("Неверные учетные данные");
            }
            throw e;
        }
    }

    public JwtResponse validateToken(String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.error("Неверный или отсутствующий заголовок Authorization");
                throw new AuthorizationHeaderException("Неверный или отсутствующий заголовок Authorization");
            }

            String token = jwtUtil.getJwtFromHeader(authHeader);

            if (!jwtUtil.validateJwtToken(token)) {
                log.warn("Валидация токена не пройдена");
                throw new InvalidTokenException("Неверный токен");
            }
    
            String username = jwtUtil.getUserNameFromJwtToken(token);
            Set<Role> roles = new HashSet<>(jwtUtil.getRoles(token));
            UUID userId = jwtUtil.getUserIdFromJwtToken(token);
    
            log.info("Токен успешно проверен: username={}, userId={}", username, userId);
            return JwtResponse.builder()
                .token(token)
                .id(userId)
                .username(username)
                .roles(roles)
                .build();
        } catch (JwtException e) { 
            log.error("Ошибка разбора токена: {}", e.getMessage());
            throw new InvalidTokenException("Неверный токен: " + e.getMessage());
        } catch (Exception e) {
            log.error("Неожиданная ошибка при валидации токена", e);
            throw new InvalidTokenException("Ошибка при валидации токена: " + e.getMessage());
        }
    }


    @Transactional
    public JwtResponse assignRole(AssignRoleRequest assignRoleRequest) {
        User user = userRepository.findByUsername(assignRoleRequest.username())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        user.getRoles().add(assignRoleRequest.role());
        User savedUser = userRepository.save(user);

        kafkaProducerService.sendUserRoleUpdateEvent(
                savedUser.getId(),
                new UserRoleUpdateEvent(
                        savedUser.getId(),
                        assignRoleRequest.role(),
                        RoleAction.ADD
                )
        );

        return userMapper.toJwtResponse(user, jwtUtil);
    }

    @Override
    @Transactional
    public JwtResponse removeRole(RemoveRoleRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        user.getRoles().remove(request.role());
        User savedUser = userRepository.save(user);

        kafkaProducerService.sendUserRoleUpdateEvent(
                savedUser.getId(),
                new UserRoleUpdateEvent(
                        savedUser.getId(),
                        request.role(),
                        RoleAction.REMOVE
                )
        );

        return userMapper.toJwtResponse(user, jwtUtil);
    }

    public User findUserById(UUID id) {
        return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("Пользователь с не найден с id: " + id));
    }

    @Override
    @Transactional
    public void handleUserCreation(UserCreatedEvent event) {
        if (userRepository.existsById(event.id())) {
            throw new UserAlreadyExistsException("Пользователь уже существует с id: " + event.id());
        }
        User user = userMapper.toUser(event, passwordEncoder);
        userRepository.insertWithCustomId(user.getId(),user.getUsername(),user.getPassword(),user.getEmail());
        user.getRoles().forEach(role -> {userRepository.insertUserRole(user.getId(),role.name());});
    }

    @Override
    @Transactional
    public void handleUserDelete(UserDeletedEvent event) {
        log.info("Обработка события удаления пользователя {}", event);
        User user = findUserById(event.id());
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void handleUserProfileUpdate(UserProfileUpdatedEvent event) {
        log.info("Обработка события обновления пользователя {}", event);
        User user = findUserById(event.id());
        userMapper.updateUser(event,user,passwordEncoder);
        userRepository.save(user);
    }

}
