package com.alibaba.druid.filter.wall.spi;

import static com.alibaba.druid.filter.wall.spi.WallVisitorUtils.loadResource;

import com.alibaba.druid.filter.wall.WallConfig;
import com.alibaba.druid.filter.wall.WallProvider;
import com.alibaba.druid.filter.wall.WallVisitor;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class OracleWallProvider extends WallProvider {

    public OracleWallProvider(){
        this(new WallConfig());
    }

    public OracleWallProvider(WallConfig config){
        this(config, true, true);
    }

    public OracleWallProvider(WallConfig config, boolean loadDefault, boolean loadExtend){
        super(config);

        if (loadDefault) {
            loadDefault();
        }

        if (loadExtend) {
            loadExtend();
        }
    }

    public void loadExtend() {
        loadResource(config.getPermitNames(), "META-INF/druid/wall/oracle/permit-name.txt");
        loadResource(config.getPermitSchemas(), "META-INF/druid/wall/oracle/permit-schema.txt");
        loadResource(config.getPermitFunctions(), "META-INF/druid/wall/oracle/permit-function.txt");
        loadResource(config.getPermitTables(), "META-INF/druid/wall/oracle/permit-table.txt");
        loadResource(config.getPermitObjects(), "META-INF/druid/wall/oracle/permit-object.txt");
    }

    public void loadDefault() {
        loadResource(config.getPermitNames(), "META-INF/druid/wall/oracle/permit-name-default.txt");
        loadResource(config.getPermitSchemas(), "META-INF/druid/wall/oracle/permit-schema-default.txt");
        loadResource(config.getPermitFunctions(), "META-INF/druid/wall/oracle/permit-function-default.txt");
        loadResource(config.getPermitTables(), "META-INF/druid/wall/oracle/permit-table-default.txt");
        loadResource(config.getPermitObjects(), "META-INF/druid/wall/oracle/permit-object-default.txt");
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
