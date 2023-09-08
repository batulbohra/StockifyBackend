package com.progsa.dao;

import com.progsa.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, String> {
    // You can add custom query methods here if needed
}
