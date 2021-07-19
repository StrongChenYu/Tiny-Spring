package com.csu.springframework.beans;

import java.util.ArrayList;
import java.util.List;

public class PropertyValues {

    private List<PropertyValue> propertyValueList = new ArrayList<>();

    public void addPropertyValue(PropertyValue pv) {
        propertyValueList.add(pv);
    }

    public PropertyValue getPropertyValue(String propertyValue) {
        for (PropertyValue value : propertyValueList) {
            if (value.getValue().equals(propertyValue)) {
                return value;
            }
        }
        return null;
    }

    public List<PropertyValue> getPropertyValueList() {
        return propertyValueList;
    }

}
