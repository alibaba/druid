package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.DbType;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallProviderCreator;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

public class Test02WallProviderCreator implements WallProviderCreator {

    private static final Log LOG = LogFactory.getLog(Test02WallProviderCreator.class);

    @Override
    public WallProvider createWallConfig(DataSourceProxy dataSource, WallConfig config, DbType dbType) {
        LOG.warn("dbType is nomatch so return NoMatchDbWallProvider" + config + "|" + dbType);
        if (config == null) {
            config = new WallConfig(MySqlWallProvider.DEFAULT_CONFIG_DIR);
        }
        return new NoMatchDbWallProvider(config, dbType);
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
