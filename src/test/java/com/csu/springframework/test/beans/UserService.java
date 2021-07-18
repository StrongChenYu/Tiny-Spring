package com.csu.springframework.test.beans;

public class UserService {

    private String name;
    private UserDao userDao;


    public UserService(String name) {
        this.name = name;
    }

    public UserService() {

    }

    public void queryUserInfo() {
        System.out.println("查询用户信息！" + name);
    }


    public void queryUserInfo(String userId) {
        System.out.println("查询用户信息！" + userDao.queryUserInfoByUserId(userId));
    }


    @Override
    public String toString() {
        return String.valueOf(hashCode());
    }
}
