package com.alibaba.druid.hdriver;

import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

import com.alibaba.druid.hdriver.impl.jdbc.HBaseConnectionImpl;

public class HDriver implements Driver {

    public static String PREFIX = "jdbc:druid-hbase:";

    @Override
    public HBaseConnectionImpl connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }

        String rest = url.substring(PREFIX.length());

        HEngine engine = HEngine.getHEngine(url, info);

        info.put("hbase.zookeeper.quorum", rest);

        return new HBaseConnectionImpl(engine, info);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        if (url == null) {
            return false;
        }

        return url.startsWith(PREFIX);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return null;
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

}
