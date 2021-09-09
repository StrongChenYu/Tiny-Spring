package com.csu.springframework.aop;

public interface PointCutAdvisor extends Advisor {
    Pointcut getPointCut();
}
