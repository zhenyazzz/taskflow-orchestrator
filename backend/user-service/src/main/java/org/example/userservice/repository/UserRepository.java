package org.example.userservice.repository;

import jakarta.transaction.Transactional;

import org.example.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = """
    INSERT INTO users (id, username,email, first_name, last_name, phone, status) 
    VALUES (:id, :username, :email, :firstName, :lastName, :phone, :status)
    """, nativeQuery = true)
    void insertWithCustomId(
            @Param("id") UUID id,
            @Param("username") String username,
            @Param("email") String email,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("phone") String phone,
            @Param("status") String status
    );

    @Modifying
    @Transactional
    @Query(value = """
    INSERT INTO user_roles (user_id, roles) 
    VALUES (?1, ?2)
    """, nativeQuery = true)
    void insertUserRole(UUID userId, String role);

} 