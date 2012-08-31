package com.alibaba.druid.filter.config.security.tool;

/**
 * @author Jonas Yang
 */
public interface Action {

    public String getId();

    public void execute();
}
