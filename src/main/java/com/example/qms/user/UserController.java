package com.example.qms.user;

import com.example.qms.user.dto.LoginDTO;
import com.example.qms.user.dto.RegistrationDTO;
import com.example.qms.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        return ResponseEntity.ok(token);
    }


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Validated @RequestBody RegistrationDTO registrationDTO) {
        try {
            userService.registerUser(registrationDTO);
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
