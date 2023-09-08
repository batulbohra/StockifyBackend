package com.progsa.dao;

import com.progsa.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class UserInfoDaoImpl implements UserInfoDao {

    private final UserInfoRepository userInfoRepository;

    @Autowired
    public UserInfoDaoImpl(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public boolean emailExists(String email) {
        return userInfoRepository.existsById(email);
    }

    @Override
    @Transactional
    public UserInfo createUser(UserInfo UserInfo) {
        return userInfoRepository.save(UserInfo);
    }

    @Override
    public UserInfo getUserByEmail(String email) {
        Optional<UserInfo> userOptional = userInfoRepository.findById(email);
        return userOptional.orElse(null);
    }

    @Override
    @Transactional
    public void updateUserDetails(UserInfo user) {
        userInfoRepository.save(user);
    }
}
