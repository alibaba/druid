package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallVisitor;

public class NullWallProvider extends WallProvider {
    public NullWallProvider(WallConfig config) {
        super(config);
    }

    public NullWallProvider(WallConfig config, DbType dbType) {
        super(config, dbType);
    }

    @Override
    public SQLStatementParser createParser(String sql) {
        return null;
    }

    @Override
    public WallVisitor createWallVisitor() {
        return null;
    }

    @Override
    public ExportParameterVisitor createExportParameterVisitor() {
        return null;
    }
}
