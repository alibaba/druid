package com.alibaba.druid.wall.spi;

import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.MSSQLServerExportParameterVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallVisitor;

/**
 * SQLServerProvider
 * 
 * @author RaymondXiu
 * @version 1.0, 2012-3-17
 * @see
 */
public class SQLServerProvider extends WallProvider {

    public final static String DEFAULT_CONFIG_DIR = "META-INF/druid/wall/sqlserver";

    /**
     * @param config
     */
    public SQLServerProvider(){
        this(new WallConfig(DEFAULT_CONFIG_DIR));
    }

    public SQLServerProvider(WallConfig config){
        super(config);
    }

    @Override
    public SQLStatementParser createParser(String sql) {
        return new SQLServerStatementParser(sql);
    }

    @Override
    public WallVisitor createWallVisitor() {
        return new SQLServerWallVisitor(config);
    }

    @Override
    public ExportParameterVisitor createExportParameterVisitor() {
        return new MSSQLServerExportParameterVisitor();
    }
}
