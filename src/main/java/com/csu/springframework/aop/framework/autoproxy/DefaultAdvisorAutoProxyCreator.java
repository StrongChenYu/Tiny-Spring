package com.csu.springframework.aop.framework.autoproxy;

import com.csu.springframework.aop.*;
import com.csu.springframework.aop.aspectj.AspectJExpressionPointCutAdvisor;
import com.csu.springframework.aop.framework.ProxyFactory;
import com.csu.springframework.beans.BeansException;
import com.csu.springframework.beans.PropertyValues;
import com.csu.springframework.beans.factory.BeanFactory;
import com.csu.springframework.beans.factory.BeanFactoryAware;
import com.csu.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import com.csu.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class DefaultAdvisorAutoProxyCreator implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    private DefaultListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * instantiation 创建这个对象之前
     * initialization
     * @param clazz
     * @param beanName
     * @return
     */
    @Override
    public Object postProcessBeforeInstantiation(Class<?> clazz, String beanName) throws BeansException {
        if (isInfrastructureClass(clazz)) {
            return null;
        }

        Collection<AspectJExpressionPointCutAdvisor> advisors = beanFactory.getBeansOfType(AspectJExpressionPointCutAdvisor.class).values();
        for (AspectJExpressionPointCutAdvisor advisor : advisors) {
            ClassFilter classFilter = advisor.getPointCut().getClassFilter();
            // 先判断满足不满足类的过滤条件
            if (!classFilter.matches(clazz)) {
                continue;
            }

            AdvisedSupport support = new AdvisedSupport();
            TargetSource source = null;
            try {
                source = new TargetSource(clazz.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }

            support.setTargetSource(source);
            support.setMethodMatcher(advisor.getPointCut().getMethodMatcher());
            support.setMethodInterceptor((MethodInterceptor) advisor.getAdvice());

            //如果类没有接口，那么使用CGLib代理
            support.setProxyTargetClass(false);

            return new ProxyFactory(support).getProxy();
        }

        return null;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        return null;
    }

    private boolean isInfrastructureClass(Class<?> clazz) {
        return Advice.class.isAssignableFrom(clazz)
                || Pointcut.class.isAssignableFrom(clazz)
                || Advisor.class.isAssignableFrom(clazz);
    }
}
