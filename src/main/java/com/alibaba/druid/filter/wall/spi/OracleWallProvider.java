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
        loadResource(config.getPermitNames(), "META-INF/druid-filter-wall-permit-name-oracle.txt");
        loadResource(config.getPermitSchemas(), "META-INF/druid-filter-wall-permit-schema-oracle.txt");
        loadResource(config.getPermitFunctions(), "META-INF/druid-filter-wall-permit-function-oracle.txt");
        loadResource(config.getPermitTables(), "META-INF/druid-filter-wall-permit-table-oracle.txt");
        loadResource(config.getPermitObjects(), "META-INF/druid-filter-wall-permit-object-oracle.txt");
    }

    public void loadDefault() {
        loadResource(config.getPermitNames(), "META-INF/druid-filter-wall-permit-name-oracle-default.txt");
        loadResource(config.getPermitSchemas(), "META-INF/druid-filter-wall-permit-schema-oracle-default.txt");
        loadResource(config.getPermitFunctions(), "META-INF/druid-filter-wall-permit-function-oracle-default.txt");
        loadResource(config.getPermitTables(), "META-INF/druid-filter-wall-permit-table-oracle-default.txt");
        loadResource(config.getPermitObjects(), "META-INF/druid-filter-wall-permit-object-oracle-default.txt");
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
