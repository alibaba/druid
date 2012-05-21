package com.alibaba.druid.hdriver.impl.mapping;

public class HMappingColumn {

    private String            name;
    private Class<?>          type;
    private transient boolean key = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }

}
