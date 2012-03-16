package com.alibaba.druid.filter.wall.spi;

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
        WallVisitorUtils.loadResource(this.permitNames, "META-INF/druid-filter-wall-permit-name-oracle.txt");
        WallVisitorUtils.loadResource(this.permitSchemas, "META-INF/druid-filter-wall-permit-schema-oracle.txt");
        WallVisitorUtils.loadResource(this.permitFunctions, "META-INF/druid-filter-wall-permit-function-oracle.txt");
        WallVisitorUtils.loadResource(this.permitTables, "META-INF/druid-filter-wall-permit-table-oracle.txt");
        WallVisitorUtils.loadResource(this.permitObjects, "META-INF/druid-filter-wall-permit-object-oracle.txt");
    }

    public void loadDefault() {
        WallVisitorUtils.loadResource(this.permitNames, "META-INF/druid-filter-wall-permit-name-oracle-default.txt");
        WallVisitorUtils.loadResource(this.permitSchemas, "META-INF/druid-filter-wall-permit-schema-oracle-default.txt");
        WallVisitorUtils.loadResource(this.permitFunctions,
                                      "META-INF/druid-filter-wall-permit-function-oracle-default.txt");
        WallVisitorUtils.loadResource(this.permitTables, "META-INF/druid-filter-wall-permit-table-oracle-default.txt");
        WallVisitorUtils.loadResource(this.permitObjects, "META-INF/druid-filter-wall-permit-object-oracle-default.txt");
    }

    @Override
    public SQLStatementParser createParser(String sql) {
        return new OracleStatementParser(sql);
    }

    @Override
    public WallVisitor createWallVisitor() {
        return new OracleWallVisitor(this);
    }

}
