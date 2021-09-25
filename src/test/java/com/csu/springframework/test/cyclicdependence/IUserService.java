package com.csu.springframework.test.cyclicdependence;

public interface IUserService {
    IUserService getIUserService();
    void outPutOwnCode();
}
