package com.example.qms.user;

import com.example.qms.user.dto.LoginDTO;
import com.example.qms.user.dto.LoginResponseDTO;
import com.example.qms.user.dto.RegistrationDTO;
import com.example.qms.user.exceptions.EmailTakenExcepetion;
import com.example.qms.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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

            return ResponseEntity.ok("User registered successfully");
        } catch (EmailTakenExcepetion e) {

            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
