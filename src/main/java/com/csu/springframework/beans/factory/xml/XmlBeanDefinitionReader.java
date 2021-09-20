package com.csu.springframework.beans.factory.xml;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.csu.springframework.beans.BeansException;
import com.csu.springframework.beans.PropertyValue;
import com.csu.springframework.beans.factory.config.BeanDefinition;
import com.csu.springframework.beans.factory.config.BeanReference;
import com.csu.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import com.csu.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.csu.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import com.csu.springframework.core.io.Resource;
import com.csu.springframework.core.io.ResourceLoader;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.w3c.dom.NodeList;
import org.dom4j.Document;
import org.dom4j.Element;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {


    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader loader) {
        super(registry, loader);
    }

    @Override
    public void loadBeanDefinition(Resource resource) throws BeansException {
        try {
            try (InputStream inputStream = resource.getInputStream()) {
                doLoadBeanDefinitions(inputStream);
            }
        } catch (IOException | ClassNotFoundException | DocumentException e) {
            throw new BeansException("IOException parsing XML document from " + resource, e);
        }
    }


    @Override
    public void loadBeanDefinition(Resource... resources) throws BeansException {
        for (Resource resource : resources) {
            loadBeanDefinition(resource);
        }
    }

    @Override
    public void loadBeanDefinition(String path) throws BeansException {
        ResourceLoader resourceLoader = getResourceLoader();
        Resource resource = resourceLoader.getResource(path);
        loadBeanDefinition(resource);
    }

    public void loadBeanDefinition(String[] configLocations) {
        for (String configLocation : configLocations) {
            loadBeanDefinition(configLocation);
        }
    }


    protected void doLoadBeanDefinitions(InputStream inputStream) throws ClassNotFoundException, DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        Element root = document.getRootElement();

        // 解析component-scan
        Element componentScan = root.element("component-scan");
        if (componentScan != null) {
            String scanPath = componentScan.attributeValue("base-package");
            if (StrUtil.isEmpty(scanPath)) {
                throw new BeansException("The value of base-package attribute can not be empty or null");
            }
            scanPackage(scanPath);
        }

        List<Element> beanList = root.elements("bean");
        for (Element element: beanList) {

            String beanName = generateBeanName(element, getRegistry());
            Class<?> clazz = generateClass(element);

            BeanDefinition beanDefinition = new BeanDefinition(clazz);

            //添加scope
            addScopeToBeanDefinition(beanDefinition, element);

            //添加init-method
            addInitMethodToBeanDefinition(beanDefinition, element);

            //添加destroy-method
            addDestroyMethodToBeanDefinition(beanDefinition, element);

            //添加property属性
            addPropertyToBeanDefinition(beanDefinition, element);

            //注册beanDefinition
            getRegistry().registerBeanDefinition(beanName, beanDefinition);
        }
    }

    private void scanPackage(String scanPath) {
        String[] paths = StrUtil.splitToArray(scanPath, ',');
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(getRegistry());
        scanner.doScan(paths);
    }

    private void addScopeToBeanDefinition(BeanDefinition beanDefinition, Element element) {
        String scope = element.attributeValue("scope");
        if (StrUtil.isNotEmpty(scope)) {
            beanDefinition.setScope(scope);
        }
    }

    private void addDestroyMethodToBeanDefinition(BeanDefinition beanDefinition, Element element) {
        String destroyMethodName = element.attributeValue("destroy-method");
        beanDefinition.setDestroyMethodName(destroyMethodName);
    }

    private void addInitMethodToBeanDefinition(BeanDefinition beanDefinition, Element element) {
        String initMethodName = element.attributeValue("init-method");
        beanDefinition.setInitMethodName(initMethodName);
    }

    private String generateBeanName(Element element, BeanDefinitionRegistry registry) throws ClassNotFoundException {
        String id = element.attributeValue("id");
        String name = element.attributeValue("name");
        Class<?> clazz = generateClass(element);
        String beanName = StrUtil.isNotEmpty(id) ? id : name;

        if (StrUtil.isEmpty(beanName)) {
            beanName = StrUtil.lowerFirst(clazz.getSimpleName());
        }

        if (registry.containsBeanDefinition(beanName)) {
            throw new BeansException("Duplicate beanName[" + beanName + "] is not allowed");
        }

        return beanName;
    }

    private Class<?> generateClass(Element element) throws ClassNotFoundException {
        String className = element.attributeValue("class");
        Class<?> clazz = Class.forName(className);
        return clazz;
    }

    private void addPropertyToBeanDefinition(BeanDefinition beanDefinition, Element element) {
        List<Element> elementProperties = element.elements("property");
        for (Element elementProperty: elementProperties) {

            String attrName = elementProperty.attributeValue("name");
            String attrValue = elementProperty.attributeValue("value");
            String attrRef = elementProperty.attributeValue("ref");

            Object value = StrUtil.isEmpty(attrRef) ? attrValue : new BeanReference(attrRef);

            PropertyValue propertyValue = new PropertyValue(attrName, value);
            beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
        }
    }

}
