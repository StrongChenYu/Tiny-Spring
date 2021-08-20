package com.csu.springframework.beans.factory.xml;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.csu.springframework.beans.BeansException;
import com.csu.springframework.beans.PropertyValue;
import com.csu.springframework.beans.factory.config.BeanDefinition;
import com.csu.springframework.beans.factory.config.BeanReference;
import com.csu.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import com.csu.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.csu.springframework.core.io.Resource;
import com.csu.springframework.core.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;

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
        } catch (IOException | ClassNotFoundException e) {
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


    protected void doLoadBeanDefinitions(InputStream inputStream) throws ClassNotFoundException {
        Document document = XmlUtil.readXML(inputStream);
        Element root = document.getDocumentElement();

        NodeList childNodes = root.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!beanIsElementSpecifiedByName(item, "bean")) {
                continue;
            }

            Element element = (Element) item;
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

    private void addScopeToBeanDefinition(BeanDefinition beanDefinition, Element element) {
        String scope = element.getAttribute("scope");
        if (StrUtil.isNotEmpty(scope)) {
            beanDefinition.setScope(scope);
        }
    }

    private void addDestroyMethodToBeanDefinition(BeanDefinition beanDefinition, Element element) {
        String destroyMethodName = element.getAttribute("destroy-method");
        beanDefinition.setDestroyMethodName(destroyMethodName);
    }

    private void addInitMethodToBeanDefinition(BeanDefinition beanDefinition, Element element) {
        String initMethodName = element.getAttribute("init-method");
        beanDefinition.setInitMethodName(initMethodName);
    }

    private String generateBeanName(Element element, BeanDefinitionRegistry registry) throws ClassNotFoundException {
        String id = element.getAttribute("id");
        String name = element.getAttribute("name");
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
        String className = element.getAttribute("class");
        Class<?> clazz = Class.forName(className);
        return clazz;
    }

    private void addPropertyToBeanDefinition(BeanDefinition beanDefinition, Element element) {
        for (int j = 0; j < element.getChildNodes().getLength(); j++) {
            Node propertyItem = element.getChildNodes().item(j);

            if (!beanIsElementSpecifiedByName(propertyItem, "property")) {
                continue;
            }

            Element property = (Element) propertyItem;
            String attrName = property.getAttribute("name");
            String attrValue = property.getAttribute("value");
            String attrRef = property.getAttribute("ref");

            Object value = StrUtil.isEmpty(attrRef) ? attrValue : new BeanReference(attrRef);

            PropertyValue propertyValue = new PropertyValue(attrName, value);
            beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
        }
    }

    private boolean beanIsElementSpecifiedByName(Node item, String name) {
        return (item instanceof Element) && (item.getNodeName().equals(name));
    }
}
