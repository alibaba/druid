package com.alibaba.druid.pool;

import java.sql.Connection;
import java.util.Properties;

/**
 * @author wenshao<szujobs@hotmail.com>
 * @since 0.2.21
 */
public class ValidConnectionCheckerAdapter implements ValidConnectionChecker {

    @Override
    public boolean isValidConnection(Connection c, String query, int validationQueryTimeout) {
        return false;
    }

    @Override
    public void configFromProperties(Properties properties) {
        
    }

}
