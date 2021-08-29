package com.csu.springframework.test.aop;

import com.csu.springframework.aop.AdvisedSupport;
import com.csu.springframework.aop.TargetSource;
import com.csu.springframework.aop.aspectj.AspectJExpressionPointcut;
import com.csu.springframework.aop.framework.Cglib2AopProxy;
import com.csu.springframework.aop.framework.JDKDynamicAopProxy;
import org.junit.Test;

import java.lang.reflect.Method;

public class AopTest {

    @Test
    public void testAspectJExpressionPointcut() throws NoSuchMethodException {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut("execution(* com.csu.springframework.test.aop.UserService.request1(..))");

        Class<UserService> clazz = UserService.class;
        Method request = clazz.getDeclaredMethod("request");

        System.out.println(pointcut.matches(clazz));
        System.out.println(pointcut.matches(request, clazz));
    }

    @Test
    public void testDynamicProxy() {
        IUserService userService = new UserService();
        TargetSource source = new TargetSource(userService);

        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut("execution(* com.csu.springframework.test.aop.IUserService.*(..))");

        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setTargetSource(source);
        advisedSupport.setMethodMatcher(pointcut);
        advisedSupport.setMethodInterceptor(new UserServiceInterceptor());

        IUserService proxyJDK = (IUserService) new JDKDynamicAopProxy(advisedSupport).getProxy();
        proxyJDK.queryUserInfo();
        proxyJDK.register("chenyu");


        IUserService proxyCGLIB= (IUserService) new Cglib2AopProxy(advisedSupport).getProxy();
        proxyCGLIB.queryUserInfo();
        proxyCGLIB.register("chenyu");

    }
}
