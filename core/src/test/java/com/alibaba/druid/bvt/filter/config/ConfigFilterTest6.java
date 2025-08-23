package com.alibaba.druid.bvt.filter.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.config.ConfigFilter;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.stat.JdbcDataSourceStat;
import junit.framework.TestCase;

import java.sql.Driver;
import java.util.List;
import java.util.Properties;


public class ConfigFilterTest6 extends TestCase {
    public void testInitFastFail() {
        ConfigFilter filter = new ConfigFilter();
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> filter.init(new DataSourceProxyImpl()));
        assertEquals("ConfigLoader only support DruidDataSource", exception.getMessage());
    }


    static class DataSourceProxyImpl implements DataSourceProxy {

        @Override
        public JdbcDataSourceStat getDataSourceStat() {
            return null;
        }

        @Override
        public long getDataSourceId() {
            return 0;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getDbType() {
            return null;
        }

        @Override
        public Driver getRawDriver() {
            return null;
        }

        @Override
        public String getUrl() {
            return null;
        }

        @Override
        public String getRawJdbcUrl() {
            return null;
        }

        @Override
        public List<Filter> getProxyFilters() {
            return null;
        }

        @Override
        public long createConnectionId() {
            return 0;
        }

        @Override
        public long createStatementId() {
            return 0;
        }

        @Override
        public long createResultSetId() {
            return 0;
        }

        @Override
        public long createMetaDataId() {
            return 0;
        }

        @Override
        public long createTransactionId() {
            return 0;
        }

        @Override
        public Properties getConnectProperties() {
            return null;
        }
    }
}
