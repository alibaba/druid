package com.alibaba.druid.filter.wall.spi;

import static com.alibaba.druid.filter.wall.spi.WallVisitorUtils.loadResource;

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
        loadResource(config.getPermitNames(), "META-INF/druid/wall/mysql/permit-name.txt");
        loadResource(config.getPermitSchemas(), "META-INF/druid/wall/mysql/permit-schema.txt");
        loadResource(config.getPermitFunctions(), "META-INF/druid/wall/mysql/permit-function.txt");
        loadResource(config.getPermitTables(), "META-INF/druid/wall/mysql/permit-table.txt");
        loadResource(config.getPermitObjects(), "META-INF/druid/wall/mysql/permit-object.txt");
    }

    public void loadDefault() {
        loadResource(config.getPermitNames(), "META-INF/druid/wall/mysql/permit-name-default.txt");
        loadResource(config.getPermitSchemas(), "META-INF/druid/wall/mysql/permit-schema-default.txt");
        loadResource(config.getPermitFunctions(), "META-INF/druid/wall/mysql/permit-function-default.txt");
        loadResource(config.getPermitTables(), "META-INF/druid/wall/mysql/permit-table-default.txt");
        loadResource(config.getPermitObjects(), "META-INF/druid/wall/mysql/permit-object-default.txt");
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
