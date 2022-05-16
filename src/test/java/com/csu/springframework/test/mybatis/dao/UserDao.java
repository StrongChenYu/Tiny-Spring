package com.csu.springframework.test.mybatis.dao;

import com.csu.springframework.test.mybatis.po.User;

public interface UserDao {

    User queryUserInfoById(Long id);
}
