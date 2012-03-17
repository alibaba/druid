package com.alibaba.druid.filter.wall.spi;

import com.alibaba.druid.filter.wall.WallConfig;
import com.alibaba.druid.filter.wall.WallProvider;
import com.alibaba.druid.filter.wall.WallVisitor;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleLexer;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;

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
        OracleLexer lexer = new OracleLexer(sql);
        lexer.setAllowComment(false);
        return new OracleStatementParser(lexer);
    }

    @Override
    public WallVisitor createWallVisitor() {
        return new OracleWallVisitor(config);
    }

}
