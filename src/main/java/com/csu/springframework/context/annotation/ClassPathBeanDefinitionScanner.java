package com.csu.springframework.context.annotation;

import cn.hutool.core.util.StrUtil;
import com.csu.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.csu.springframework.beans.factory.config.BeanDefinition;
import com.csu.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.Set;

public class ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider {

    private BeanDefinitionRegistry registry;

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public void doScan(String... basePackages) {
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = findCandidateComponents(basePackage);

            for (BeanDefinition beanDefinition : candidateComponents) {
                String beanScope = resolveBeanScope(beanDefinition);

                if (StrUtil.isNotEmpty(beanScope)) {
                    beanDefinition.setScope(beanScope);
                }

                registry.registerBeanDefinition(determineBeanName(beanDefinition), beanDefinition);
            }
        }

        registry.registerBeanDefinition("com.csu.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor", new BeanDefinition(AutowiredAnnotationBeanPostProcessor.class));
    }

    /**
     * 获取@Component注解中的value,没有默认为类名的首字母小写结果
     * @param beanDefinition
     * @return
     */
    private String determineBeanName(BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Component annotation = beanClass.getAnnotation(Component.class);
        String value = annotation.value();
        if (StrUtil.isEmpty(value)) {
            return StrUtil.lowerFirst(beanClass.getSimpleName());
        }
        return value;
    }

    /**
     * 获取@Scope中的配置
     * @param beanDefinition
     * @return
     */
    private String resolveBeanScope(BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Scope annotation = beanClass.getAnnotation(Scope.class);
        if (annotation != null) {
            return annotation.value();
        }
        return StrUtil.EMPTY;
    }



}
