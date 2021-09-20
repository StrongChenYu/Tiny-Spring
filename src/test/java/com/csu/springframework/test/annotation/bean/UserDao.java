package com.csu.springframework.test.annotation.bean;

import com.csu.springframework.context.annotation.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserDao {

    private final static Map<String, String> map = new ConcurrentHashMap<>();

    static {
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
