package com.progsa.dao;

import com.progsa.model.UserInfo;

public interface UserInfoDao  {

        boolean emailExists(String email);

        UserInfo createUser(UserInfo user);

        UserInfo getUserByEmail(String email);

        void updateUserDetails(UserInfo userInfo);
}
