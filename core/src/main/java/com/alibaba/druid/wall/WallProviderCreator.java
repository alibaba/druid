package com.alibaba.druid.wall;

import com.alibaba.druid.DbType;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;

/**
 * @author lizongbo
 * @since 1.2.22
 */
public interface WallProviderCreator {
    /**
     * @param dataSource mabye exists wall config
     * @param config     maybe null
     * @param dbType     maybe null
     * @return
     */
    WallProvider createWallConfig(DataSourceProxy dataSource, WallConfig config, DbType dbType);

    default int getOrder() {
        return 0;
    }

}
