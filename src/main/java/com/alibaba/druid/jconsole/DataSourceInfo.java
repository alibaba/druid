package com.alibaba.druid.jconsole;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.management.openmbean.CompositeData;

public class DataSourceInfo {

    private long         id;
    private String       name;
    private String       url;
    private List<String> filters = new ArrayList<String>();
    private Date         createdTime;

    private String       rawDriverClassName;
    private String       rawUrl;
    private int       rawDriverMajorVersion;
    private int       rawDriverMinorVersion;
    private String       properties;

    public DataSourceInfo(){

    }

    public DataSourceInfo(CompositeData data){
        this.id = ((Number) data.get("ID")).longValue();
        this.name = (String) data.get("Name");
        this.url = (String) data.get("URL");
        for (String item : (String[]) data.get("FilterClasses")) {
            filters.add(item);
        }
        this.createdTime = (Date) data.get("CreatedTime");

        this.rawDriverClassName = (String) data.get("RawDriverClassName");
        this.rawUrl = (String) data.get("RawUrl");
        this.rawDriverMajorVersion = ((Number) data.get("RawDriverMajorVersion")).intValue();
        this.rawDriverMinorVersion = ((Number) data.get("RawDriverMinorVersion")).intValue();
        this.properties = (String) data.get("Properties");
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getFilters() {
        return filters;
    }

    public void setFilters(List<String> filters) {
        this.filters = filters;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getRawDriverClassName() {
        return rawDriverClassName;
    }

    public void setRawDriverClassName(String rawDriverClassName) {
        this.rawDriverClassName = rawDriverClassName;
    }

    public String getRawUrl() {
        return rawUrl;
    }

    public void setRawUrl(String rawUrl) {
        this.rawUrl = rawUrl;
    }

    public int getRawDriverMajorVersion() {
        return rawDriverMajorVersion;
    }

    public void setRawDriverMajorVersion(int rawDriverMajorVersion) {
        this.rawDriverMajorVersion = rawDriverMajorVersion;
    }

    public int getRawDriverMinorVersion() {
        return rawDriverMinorVersion;
    }

    public void setRawDriverMinorVersion(int rawDriverMinorVersion) {
        this.rawDriverMinorVersion = rawDriverMinorVersion;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

}
