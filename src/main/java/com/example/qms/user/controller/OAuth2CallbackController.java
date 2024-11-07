package com.example.qms.user.controller;

import com.example.qms.user.User;
import com.example.qms.user.UserRepository;
import com.example.qms.user.services.OAuth2UserService;
import com.example.qms.utils.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/oauth2/callback")
public class OAuth2CallbackController {

    @Autowired
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private final OAuth2AuthorizedClientService authorizedClientService;
    @Autowired
    private final OAuth2UserService oauth2UserService;

    public OAuth2CallbackController(JwtTokenProvider jwtTokenProvider,
                                    OAuth2AuthorizedClientService authorizedClientService,
                                    OAuth2UserService oauth2UserService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authorizedClientService = authorizedClientService;
        this.oauth2UserService = oauth2UserService;
    }

    @GetMapping("/google")
    public Map<String, String> googleCallback(OAuth2AuthenticationToken authentication)
    {
        if (authentication == null) {
            throw new IllegalArgumentException("Authentication object is null");
        }

        // Get the registered client ID and user details
        String registrationId = authentication.getAuthorizedClientRegistrationId();

        // Fetch the authorized client
        OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient(registrationId, authentication.getName());

        // Get user details from the authentication token
        OAuth2User oauthUser = authentication.getPrincipal();

        // Process and save OAuth2 user
        User user = oauth2UserService.processOAuthUser(oauthUser);

        // Generate JWT token
        String jwtToken = jwtTokenProvider.generateToken((Authentication) user);

        return Map.of("token", jwtToken);
    }
}
