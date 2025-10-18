package org.example.userservice.repository;

import jakarta.transaction.Transactional;

import org.example.userservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = """
           SELECT u.* FROM users u
           LEFT JOIN user_roles ur ON u.id = ur.user_id
           WHERE (:username IS NULL OR LOWER(CAST(u.username AS TEXT)) LIKE LOWER(CONCAT('%', :username, '%'))) AND
                 (:email IS NULL OR LOWER(CAST(u.email AS TEXT)) LIKE LOWER(CONCAT('%', :email, '%'))) AND
                 (:role IS NULL OR :role = ur.roles)
           GROUP BY u.id
           """, 
           countQuery = """
           SELECT count(DISTINCT u.id) FROM users u
           LEFT JOIN user_roles ur ON u.id = ur.user_id
           WHERE (:username IS NULL OR LOWER(CAST(u.username AS TEXT)) LIKE LOWER(CONCAT('%', :username, '%'))) AND
                 (:email IS NULL OR LOWER(CAST(u.email AS TEXT)) LIKE LOWER(CONCAT('%', :email, '%'))) AND
                 (:role IS NULL OR :role = ur.roles)
           """,
           nativeQuery = true)
    Page<User> findUsersWithFilters(
        @Param("username") String username,
        @Param("email") String email, 
        @Param("role") String role,
        Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = """
    INSERT INTO users (id, username,email, first_name, last_name, status) 
    VALUES (:id, :username, :email, :firstName, :lastName, :status)
    """, nativeQuery = true)
    void insertWithCustomId(
            @Param("id") UUID id,
            @Param("username") String username,
            @Param("email") String email,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
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