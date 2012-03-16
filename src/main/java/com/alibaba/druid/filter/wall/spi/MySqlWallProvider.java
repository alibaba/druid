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
            config.loadDefault("META-INF/druid/wall/mysql");
        }

        if (loadExtend) {
            config.loadExtend("META-INF/druid/wall/mysql");
        }
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
