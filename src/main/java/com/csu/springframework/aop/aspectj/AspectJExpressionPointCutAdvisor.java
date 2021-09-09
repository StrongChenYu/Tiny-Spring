package com.csu.springframework.aop.aspectj;

import com.csu.springframework.aop.Advisor;
import com.csu.springframework.aop.PointCutAdvisor;
import com.csu.springframework.aop.Pointcut;
import org.aopalliance.aop.Advice;

public class AspectJExpressionPointCutAdvisor implements PointCutAdvisor {

    private AspectJExpressionPointcut pointcut;
    private Advice advice;
    private String expression;

    @Override
    public Advice getAdvice() {
        return advice;
    }

    @Override
    public Pointcut getPointCut() {
        if (pointcut == null) {
            pointcut = new AspectJExpressionPointcut(expression);
        }
        return pointcut;
    }

    public void setAdvice(Advice advice) {
        this.advice = advice;
    }
}
