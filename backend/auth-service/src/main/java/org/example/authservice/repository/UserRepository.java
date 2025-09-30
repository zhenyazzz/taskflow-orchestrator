package org.example.authservice.repository;

import jakarta.transaction.Transactional;
import org.example.authservice.model.User;
import org.example.events.enums.UserStatus;
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
    boolean existsByUsername(String username);

    @Modifying
    @Transactional
    @Query(value = """
    INSERT INTO users (id, username,password,email) 
    VALUES (:id, :username, :password, :email)
    """, nativeQuery = true)
    void insertWithCustomId(
            @Param("id") UUID id,
            @Param("username") String username,
            @Param("password") String password,
            @Param("email") String email
    );

    @Modifying
    @Transactional
    @Query(value = """
    INSERT INTO user_roles (user_id, role) 
    VALUES (?1, ?2)
    """, nativeQuery = true)
    void insertUserRole(UUID userId, String role);
}