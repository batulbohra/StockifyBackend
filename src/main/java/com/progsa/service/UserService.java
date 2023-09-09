package com.progsa.service;

import com.progsa.IOModels.UserLoginResponse;
import com.progsa.dao.UserInfoDao;
import com.progsa.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.progsa.Constants.*;

import java.util.Map;

@Service
public class UserService {

    private final UserInfoDao userDao;

    @Autowired
    public UserService(UserInfoDao userDao) {
        this.userDao = userDao;
    }

    public ResponseEntity<String> signUp(UserInfo user) {
        try {
            // Validate if the email already exists
            if (userDao.emailExists(user.getEmail())) {
                return ResponseEntity.badRequest().body(EMAIL_EXISTS);
            }
            // Create the user if the email doesn't exist
            userDao.createUser(user);

            return ResponseEntity.ok(USER_CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ERROR_MESSAGE);
        }
    }

    public ResponseEntity<UserLoginResponse> login(Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        // Validate if the user exists
        UserInfo userInfo = userDao.getUserByEmail(email);
        if (userInfo == null) {
            return ResponseEntity.badRequest().body(new UserLoginResponse(USER_NOT_FOUND, null));
        }

        // Verify email and password
        boolean verificationStatus = verifyUser(userInfo, password);
        if (!verificationStatus) {
            return ResponseEntity.ok(new UserLoginResponse(INCORRECT_PASSWORD, null));
        }

        // Return user data without the password
        userInfo.setPassword(null); // Set the password to null before returning
        return ResponseEntity.ok(new UserLoginResponse(VERIFIED, userInfo));
    }

    public boolean verifyUser(UserInfo userInfo, String password) {
        return userInfo != null && userInfo.getPassword().equals(password);
    }
}