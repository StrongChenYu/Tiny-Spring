package com.csu.springframework.test.factorybean;

import com.csu.springframework.test.beans.UserDao;

import java.lang.reflect.Method;

public class UserService {

    private String uId;
    private String company;
    private String location;
    private IUserDao userDao;

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

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
        System.out.println("查询用户信息！" + userDao.queryUserName(userId));
    }


    public static void testException() throws Exception {
        try {
            UserService.class.getMethod("queryUserInfo1");
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodException("no such method");
        }
        System.out.println("chenyu");
    }

    public static void main(String[] args) throws NoSuchMethodException {
        Method queryUserInfo1 = UserService.class.getMethod("queryUserInfo1");
        System.out.println(queryUserInfo1);

    }
}
