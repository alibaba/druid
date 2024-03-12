package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.DbType;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallProviderCreator;
import com.alibaba.druid.wall.WallVisitor;

public class Test01WallProviderCreator implements WallProviderCreator {

    private static final Log LOG = LogFactory.getLog(Test01WallProviderCreator.class);
    @Override
    public WallProvider createWallConfig(DataSourceProxy dataSource, WallConfig config, DbType dbType) {
        if (dbType == null) {
            NullWallProvider nullWallProvider= new NullWallProvider(config);
            LOG.warn("dbType is null so return NullWallProvider|"+nullWallProvider);
            return nullWallProvider;
        }
        return null;
    }

    @Override
    public int getOrder() {
        return 200;
    }
}
