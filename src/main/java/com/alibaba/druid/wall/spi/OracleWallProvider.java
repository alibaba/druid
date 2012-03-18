package com.alibaba.druid.wall.spi;

import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallVisitor;

public class OracleWallProvider extends WallProvider {

    public final static String DEFAULT_CONFIG_DIR = "META-INF/druid/wall/oracle";

    public OracleWallProvider(){
        this(new WallConfig(DEFAULT_CONFIG_DIR));
    }

    public OracleWallProvider(WallConfig config){
        super(config);
    }

    @Override
    public SQLStatementParser createParser(String sql) {
        return new OracleStatementParser(sql);
    }

    @Override
    public WallVisitor createWallVisitor() {
        return new OracleWallVisitor(config);
    }

}
