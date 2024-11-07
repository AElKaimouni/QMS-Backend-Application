package com.example.qms.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/oauth2")
public class OAuth2Controller {

    @GetMapping("/google/login")
    public RedirectView googleLogin() {
        // Redirects to Google's OAuth2 authorization endpoint
        return new RedirectView("/oauth2/authorization/google");
    }
}
