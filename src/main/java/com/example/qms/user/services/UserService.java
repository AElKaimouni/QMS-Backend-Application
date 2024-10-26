package com.example.qms.user.services;

import com.example.qms.user.dto.LoginDTO;
import com.example.qms.user.dto.RegistrationDTO;
import com.example.qms.user.User;
import com.example.qms.user.UserRepository;
import com.example.qms.user.exceptions.EmailTakenExcepetion;
import com.example.qms.user.exceptions.InvalidResetTokenException;
import com.example.qms.user.exceptions.UserNotFoundException;
import com.example.qms.utils.EmailService;
import com.example.qms.utils.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    @Autowired
    private EmailService emailService;

    private String appAdresse="localhost:8080";

    public void registerUser(RegistrationDTO registrationDTO) throws EmailTakenExcepetion {
        // Check if email already exists
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new EmailTakenExcepetion();
        }

        // Create new user with encoded password
        User newUser = new User();
        newUser.setUsername(registrationDTO.getUsername()); // Add username
        newUser.setEmail(registrationDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));

        // Generate verification token
        String token = UUID.randomUUID().toString();
        newUser.setVerificationToken(token);
        // Save user to the database
        userRepository.save(newUser);
        // Send verification email
        emailService.sendVerificationEmail(newUser.getEmail(), token);
    }

    public String login(LoginDTO loginDTO) {
        // Authenticate the user
        Authentication authentication= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Load user details
        return jwtTokenProvider.generateToken(authentication);
    }

    public void initiatePasswordReset(String email) throws UserNotFoundException {
        Optional<User> userFound = userRepository.findByEmail(email);

        if (userFound.isEmpty()) throw new UserNotFoundException("No user found  ");

        User user = userFound.get();

        // Generate a password reset token (could be UUID or any token generator)
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);

        // Set the token expiration (e.g., 1 hour from now)
        user.setResetTokenExpiry(Timestamp.from(Instant.now().plus(1, ChronoUnit.HOURS)));

        // Save the user with the new token and expiration time
        userRepository.save(user);

        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
    }

    public void resetPassword(String token, String newPassword) throws InvalidResetTokenException {
        User user = userRepository.findByResetToken(token);

        if (user == null || isTokenExpired(user.getResetTokenExpiry())) {
            throw new InvalidResetTokenException("Invalid or expired token.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));  // Use the same encoder as in registration
        user.setResetToken(null);  // Invalidate the token after reset
        user.setResetTokenExpiry(null);

        userRepository.save(user);
    }

    private boolean isTokenExpired(Timestamp tokenExpiry) {
        return tokenExpiry.before(new Timestamp(System.currentTimeMillis()));
    }

    // Method to find user by reset token
    public User findUserByResetToken(String token) throws InvalidResetTokenException {
        User user = userRepository.findByResetToken(token);
        // Convert Timestamp to LocalDateTime
        LocalDateTime tokenExpiryDateTime = user.getResetTokenExpiry().toLocalDateTime();

        // Check if the token has expired
        if (tokenExpiryDateTime.isBefore(LocalDateTime.now())) {
            throw new InvalidResetTokenException("Token has expired");
        }

        return user;
    }


}
