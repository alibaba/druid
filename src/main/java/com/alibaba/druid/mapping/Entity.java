package com.alibaba.druid.mapping;

import java.util.LinkedHashMap;
import java.util.Map;

public class Entity {

    private String                name;
    private String                description;
    private String                tableName;

    private Map<String, Property> properties = new LinkedHashMap<String, Property>();

    public Entity(){
    }

    public Entity(String name, String description, String tableName){
        this.name = name;
        this.description = description;
        this.tableName = tableName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void addProperty(Property property) {
        this.properties.put(property.getName(), property);
    }

    public Property getProperty(String name) {
        Property prop = this.properties.get(name);

        if (prop == null) {
            for (Map.Entry<String, Property> entry : properties.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(name)) {
                    prop = entry.getValue();
                    break;
                }
            }
        }

        return prop;
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Property> properties) {
        this.properties = properties;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("{name=");
        buf.append(name);
        
        buf.append(", tableName=");
        buf.append(tableName);
        
        buf.append("}");

        return buf.toString();
    }

}
