package com.alibaba.druid.pool.vendor;

import com.alibaba.druid.pool.ExceptionSorter;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * oracle exception sorter abstract
 */
public abstract class AbstractOracleExceptionSorter implements ExceptionSorter {
    private static final Log LOG = LogFactory.getLog(AbstractOracleExceptionSorter.class);
    protected Set<Integer> fatalErrorCodes = new HashSet<Integer>();

    @Override
    public void configFromProperties(Properties properties) {
        if (properties == null) {
            return;
        }
        String property = properties.getProperty("druid.oracle.fatalErrorCodes");
        if (property != null) {
            String[] items = property.split("\\,");
            for (String item : items) {
                if (item != null && item.length() > 0) {
                    try {
                        int code = Integer.parseInt(item);
                        fatalErrorCodes.add(code);
                    } catch (NumberFormatException e) {
                        LOG.error("parse druid.oracle.fatalErrorCodes error", e);
                    }
                }
            }
        }
    }
}
