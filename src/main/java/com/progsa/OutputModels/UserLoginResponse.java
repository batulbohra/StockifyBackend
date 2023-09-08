package com.progsa.OutputModels;

import com.progsa.model.UserInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginResponse {
    private String status;
    private UserInfo userInfo;

    public UserLoginResponse(String status, UserInfo userInfo) {
        this.status = status;
        this.userInfo = userInfo;
    }
}
