package com.csu.springframework.test.beans;

import com.csu.springframework.beans.BeansException;
import com.csu.springframework.beans.factory.*;
import com.csu.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

public class UserService implements BeanClassLoaderAware, ApplicationContextAware, BeanNameAware, BeanFactoryAware {

    private String uId;
    private String company;
    private String location;
    private UserDao userDao;

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

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
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
        System.out.println("查询用户信息！" + userDao.queryUserInfoByUserId(userId));
    }

    @Override
    public String toString() {
        return "UserService{" +
                "uId='" + uId + '\'' +
                ", company='" + company + '\'' +
                ", location='" + location + '\'' +
                '}';
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("setApplicationContext: " + applicationContext);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        System.out.println("setBeanClassLoader: " + classLoader);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        System.out.println("setBeanFactory: " + beanFactory);
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("setBeanName: " + name);
    }
}
