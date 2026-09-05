package com.sunrise.controller;

import com.sunrise.model.User;
import com.sunrise.service.AuthService;

public class LoginController {

    private final AuthService authService;

    public LoginController() {
        this.authService = new AuthService();
    }

    public User login(String username, String password) {
        return authService.login(username, password);
    }
}