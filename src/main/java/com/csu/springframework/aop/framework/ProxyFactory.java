package com.csu.springframework.aop.framework;

import com.csu.springframework.aop.AdvisedSupport;

public class ProxyFactory implements AopProxy {
    private AdvisedSupport support;

    public ProxyFactory(AdvisedSupport support) {
        this.support = support;
    }

    @Override
    public Object getProxy() {
        return createProxy().getProxy();
    }

    private AopProxy createProxy() {
        if (support.isProxyTargetClass()) {
            return new Cglib2AopProxy(support);
        }
        return new JDKDynamicAopProxy(support);
    }
}
