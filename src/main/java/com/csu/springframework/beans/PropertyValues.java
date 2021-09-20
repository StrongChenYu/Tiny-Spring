package com.csu.springframework.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class PropertyValues {

    private Map<String, PropertyValue> propertyValueMap = new ConcurrentHashMap<>();

    public void addPropertyValue(PropertyValue pv) {
        propertyValueMap.put(pv.getName(), pv);
    }


    public PropertyValue getPropertyValue(String propertyValue) {
        return null;
    }

    public PropertyValue[] getPropertyValues() {
        return propertyValueMap.values().toArray(new PropertyValue[0]);
    }

    public Object getPropertyObject(String name) {
        return propertyValueMap.get(name);
    }

}
