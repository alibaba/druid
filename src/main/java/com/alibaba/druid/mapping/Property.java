package com.alibaba.druid.mapping;

public class Property {
    private String name;
    private String desciption;
    private String dbColumnName;

    public Property(){
    }

    public Property(String name, String desciption, String dbColumnName){
        this.name = name;
        this.desciption = desciption;
        this.dbColumnName = dbColumnName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDbColumnName() {
        return dbColumnName;
    }

    public void setDbColumnName(String dbColumnName) {
        this.dbColumnName = dbColumnName;
    }

    public String getDesciption() {
        return desciption;
    }

    public void setDesciption(String desciption) {
        this.desciption = desciption;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("{name=");
        buf.append(name);
        
        buf.append(", dbColumnName=");
        buf.append(dbColumnName);
        
        buf.append("}");

        return buf.toString();
    }
}
