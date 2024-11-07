package com.example.qms.user.config;
import com.example.qms.user.User;
import com.example.qms.user.services.OAuth2UserService;
import com.example.qms.utils.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private  OAuth2AuthorizedClientService authorizedClientService;
    @Autowired

    private  OAuth2UserService oauth2UserService;

    public OAuth2AuthenticationSuccessHandler(JwtTokenProvider jwtTokenProvider,
                                              OAuth2AuthorizedClientService authorizedClientService,
                                              OAuth2UserService oauth2UserService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authorizedClientService = authorizedClientService;
        this.oauth2UserService = oauth2UserService;
    }

    public OAuth2AuthenticationSuccessHandler() {

    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauth2Authentication = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = oauth2Authentication.getPrincipal();

        // Extract user details (e.g., email)
        String email = (String) oauthUser.getAttributes().get("email");
        if (email == null) {
            throw new ServletException("Email not found in OAuth2 attributes");
        }

        // Process user and generate JWT token
        oauth2UserService.processOAuthUser(oauthUser);
        String jwtToken = jwtTokenProvider.generateToken(authentication);  // Pass email as subject


        // Return token as response (or handle it according to your needs)
        response.setContentType("application/json");
        response.getWriter().write(Map.of("token", jwtToken).toString());
        response.getWriter().flush();
    }


}

