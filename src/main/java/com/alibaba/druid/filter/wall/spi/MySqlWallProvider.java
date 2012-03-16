package com.alibaba.druid.filter.wall.spi;

import com.alibaba.druid.filter.wall.WallProvider;
import com.alibaba.druid.filter.wall.WallVisitor;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class MySqlWallProvider extends WallProvider {

    public MySqlWallProvider(){
        loadDefault();
        
        WallVisitorUtils.loadResource(this.permitNames, "META-INF/druid-filter-wall-permit-name-mysql.txt");
        WallVisitorUtils.loadResource(this.permitSchemas, "META-INF/druid-filter-wall-permit-schema-mysql.txt");
        WallVisitorUtils.loadResource(this.permitFunctions, "META-INF/druid-filter-wall-permit-function-mysql.txt");
        WallVisitorUtils.loadResource(this.permitTables, "META-INF/druid-filter-wall-permit-table-mysql.txt");
        WallVisitorUtils.loadResource(this.permitObjects, "META-INF/druid-filter-wall-permit-object-mysql.txt");
    }
    
    public void loadDefault() {
        WallVisitorUtils.loadResource(this.permitNames, "META-INF/druid-filter-wall-permit-name-mysql-default.txt");
        WallVisitorUtils.loadResource(this.permitSchemas, "META-INF/druid-filter-wall-permit-schema-mysql-default.txt");
        WallVisitorUtils.loadResource(this.permitFunctions, "META-INF/druid-filter-wall-permit-function-mysql-default.txt");
        WallVisitorUtils.loadResource(this.permitTables, "META-INF/druid-filter-wall-permit-table-mysql-default.txt");
        WallVisitorUtils.loadResource(this.permitObjects, "META-INF/druid-filter-wall-permit-object-mysql-default.txt");
    }

    @Override
    public SQLStatementParser createParser(String sql) {
        return new MySqlStatementParser(sql);
    }

    @Override
    public WallVisitor createWallVisitor() {
        return new MySqlWallVisitor(this);
    }

}
