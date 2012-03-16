package com.alibaba.druid.filter.wall.spi;

import com.alibaba.druid.filter.wall.WallConfig;
import com.alibaba.druid.filter.wall.WallProvider;
import com.alibaba.druid.filter.wall.WallVisitor;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class MySqlWallProvider extends WallProvider {

    public MySqlWallProvider(){
        this(new WallConfig());
    }

    public MySqlWallProvider(WallConfig config){
        this(config, true, true);
    }

    public MySqlWallProvider(WallConfig config, boolean loadDefault, boolean loadExtend){
        super(config);

        if (loadDefault) {
            loadDefault();
        }

        if (loadExtend) {
            loadExtend();
        }
    }

    public void loadExtend() {
        WallVisitorUtils.loadResource(config.getPermitNames(), "META-INF/druid-filter-wall-permit-name-mysql.txt");
        WallVisitorUtils.loadResource(config.getPermitSchemas(), "META-INF/druid-filter-wall-permit-schema-mysql.txt");
        WallVisitorUtils.loadResource(config.getPermitFunctions(),
                                      "META-INF/druid-filter-wall-permit-function-mysql.txt");
        WallVisitorUtils.loadResource(config.getPermitTables(), "META-INF/druid-filter-wall-permit-table-mysql.txt");
        WallVisitorUtils.loadResource(config.getPermitObjects(), "META-INF/druid-filter-wall-permit-object-mysql.txt");
    }

    public void loadDefault() {
        WallVisitorUtils.loadResource(config.getPermitNames(),
                                      "META-INF/druid-filter-wall-permit-name-mysql-default.txt");
        WallVisitorUtils.loadResource(config.getPermitSchemas(),
                                      "META-INF/druid-filter-wall-permit-schema-mysql-default.txt");
        WallVisitorUtils.loadResource(config.getPermitFunctions(),
                                      "META-INF/druid-filter-wall-permit-function-mysql-default.txt");
        WallVisitorUtils.loadResource(config.getPermitTables(),
                                      "META-INF/druid-filter-wall-permit-table-mysql-default.txt");
        WallVisitorUtils.loadResource(config.getPermitObjects(),
                                      "META-INF/druid-filter-wall-permit-object-mysql-default.txt");
    }

    @Override
    public SQLStatementParser createParser(String sql) {
        return new MySqlStatementParser(sql);
    }

    @Override
    public WallVisitor createWallVisitor() {
        return new MySqlWallVisitor(config);
    }

}
