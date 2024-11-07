package com.example.qms.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    User findByVerificationToken(String token);

    User findByResetToken(String token);

    boolean existsByEmail(String email);

    User getUserByid(Long id);
}
