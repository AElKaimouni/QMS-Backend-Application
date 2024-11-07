package com.example.qms.user.services;

import com.example.qms.user.User;
import com.example.qms.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private  UserRepository userRepository;
    private static final String PLACEHOLDER_PASSWORD = "$tempPassword@123"; // Your placeholder password


    public User processOAuthUser(OAuth2User oauthUser) {
        String email = oauthUser.getAttribute("email");
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(oauthUser.getAttribute("name"));
                    newUser.setPassword(PLACEHOLDER_PASSWORD);
                    System.out.println("User registered from oauth2: " + newUser);
                    return userRepository.save(newUser);
                });
    }
}
