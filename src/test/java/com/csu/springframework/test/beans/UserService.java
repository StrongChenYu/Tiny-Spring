package com.csu.springframework.test.beans;

public class UserService {

    private String uId;
    private String company;
    private String location;
    private UserDao userDao;


    public UserService(String uId, String company, String location) {
        this.uId = uId;
        this.company = company;
        this.location = location;
    }

    public UserService() {

    }

    public void queryUserInfo() {
        System.out.println("查询用户信息！" + uId);
    }

    public void queryUserInfo(String userId) {
        System.out.println("查询用户信息！" + userDao.queryUserInfoByUserId(userId));
    }


    @Override
    public String toString() {
        return String.valueOf(hashCode());
    }
}
