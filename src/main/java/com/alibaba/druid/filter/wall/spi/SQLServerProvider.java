package com.alibaba.druid.filter.wall.spi;

import com.alibaba.druid.filter.wall.WallConfig;
import com.alibaba.druid.filter.wall.WallProvider;
import com.alibaba.druid.filter.wall.WallVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;

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
    public SQLServerProvider() {
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
    

}
