package com.alibaba.druid.pool;

import java.util.Properties;

/**
 * 
 * @author wenshao<szujobs@hotmail.com>
 * @since 0.2.21
 */
public abstract class DruidDataSourceStatLoggerAdapter implements DruidDataSourceStatLogger {

    @Override
    public void log(DruidDataSourceStatValue statValue) {
        
    }

    @Override
    public void configFromProperties(Properties properties) {
        
    }

}
