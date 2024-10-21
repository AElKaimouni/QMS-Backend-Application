package com.example.qms.user.controller;
import com.example.qms.user.User;
import com.example.qms.user.UserRepository;
import com.example.qms.user.exceptions.TokenNotFoundException;
import com.example.qms.user.exceptions.UserAlreadyVerifiedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerificationController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/verify")
    public String verifyUser(@RequestParam("token") String token) {
        User user = userRepository.findByVerificationToken(token);

        if (user == null) {
            throw new TokenNotFoundException("Invalid verification token");
        }

        if (user.isVerified()) {
            throw new UserAlreadyVerifiedException("User is already verified");
        }
        if (user != null) {
            user.setVerified(true);
            user.setVerificationToken(null); // Clear the token after verification
            userRepository.save(user);
            return "Email verified successfully!";
        }
        return "Invalid verification token!";
    }
}
