package com.progsa.controller;

import com.progsa.OutputModels.UserLoginResponse;
import com.progsa.model.UserInfo;
import com.progsa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static com.progsa.Constants.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value="/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> signUp(@RequestBody UserInfo user) {
        try {
            return userService.signUp(user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE, e);
        }
    }

    @PostMapping(value="/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserLoginResponse> login(@RequestBody Map<String, String> loginRequest) {
        try {
            return userService.login(loginRequest);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE, e);
        }
    }
}
