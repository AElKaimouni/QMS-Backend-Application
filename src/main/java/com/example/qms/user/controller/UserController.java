package com.example.qms.user.controller;

import com.example.qms.user.User;
import com.example.qms.user.UserRepository;
import com.example.qms.user.dto.*;
import com.example.qms.user.exceptions.*;

import com.example.qms.user.exceptions.EmailTakenExcepetion;
import com.example.qms.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginDTO loginDTO) {
        String token = userService.login(loginDTO);

        return new LoginResponseDTO(token);
    }


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Validated @RequestBody RegistrationDTO registrationDTO) {
        try {
            userService.registerUser(registrationDTO);
            return ResponseEntity.ok("User registered successfully. Please check your email to verify your account.");
        } catch (EmailTakenExcepetion e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already taken.");
        } catch (EmailSendException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send verification email.");
        } catch (TokenNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification token.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PostMapping("/password-reset-request")
    public ResponseEntity<String> requestPasswordReset(@Validated @RequestBody PasswordResetRequest request) {
        try {
            userService.initiatePasswordReset(request.getEmail());
            return ResponseEntity.ok("Password reset link sent to your email.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with this email not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while sending reset link.");
        }
    }

    @GetMapping("/reset-password")
    public ResponseEntity<String> showResetPasswordForm(@RequestParam("token") String token) {
        try {
            // Now we're calling the service layer to handle user retrieval by token
            User user = userService.findUserByResetToken(token);

            // If the user is found, return the reset password form
            return ResponseEntity.ok("<html><body><form action='/reset-password' method='post'>"
                    + "<input type='hidden' name='token' value='" + token + "' />"
                    + "New Password: <input type='password' name='newPassword' />"
                    + "<button type='submit'>Reset Password</button>"
                    + "</form></body></html>");
        } catch (InvalidResetTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error displaying reset form.");
        }
    }



    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword) {
        try {
            userService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Password reset successful.");
        } catch (InvalidResetTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired reset token.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while resetting password.");
        }
    }

}