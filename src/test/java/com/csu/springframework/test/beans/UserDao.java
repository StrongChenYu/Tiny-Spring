package com.csu.springframework.test.beans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserDao {

    private final static Map<String, String> map = new ConcurrentHashMap<>();

    public void initDataMethod(){
        System.out.println("执行：init-method");
        map.put("001", "chenyu1");
        map.put("002", "chenyu2");
        map.put("003", "chenyu3");
    }

    public void destroyDataMethod(){
        System.out.println("执行：destroy-method");
        map.clear();
    }

    public String queryUserInfoByUserId(String userId) {
        return map.get(userId);
    }
}
