package com.example.qms.user.services;

import com.example.qms.user.dto.LoginDTO;
import com.example.qms.user.dto.RegistrationDTO;
import com.example.qms.user.User;
import com.example.qms.user.UserRepository;
import com.example.qms.utils.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.HashMap;
import java.util.Map;

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


    public void registerUser(RegistrationDTO registrationDTO) throws Exception {
        // Check if email already exists
        if (userRepository.findByEmail(registrationDTO.getEmail()).isPresent()) {
            throw new Exception("Email is already taken");
        }

        // Validate password and confirm password
        if (!registrationDTO.getPassword().equals(registrationDTO.getConfirmPassword())) {
            throw new Exception("Passwords do not match");
        }

        // Create new user with encoded password
        User newUser = new User();
        newUser.setEmail(registrationDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));

        // Save user to the database
        userRepository.save(newUser);
    }

    public String login(LoginDTO loginDTO) {
        // Authenticate the user
        Authentication authentication= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Load user details
        return jwtTokenProvider.generateToken(authentication);
    }

}
