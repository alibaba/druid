package com.alibaba.druid.filter.wall.spi;

import com.alibaba.druid.filter.wall.WallProvider;
import com.alibaba.druid.filter.wall.WallVisitor;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;


public class MySqlWallProvider extends WallProvider {

    @Override
    public SQLStatementParser createParser(String sql) {
        return new MySqlStatementParser(sql);
    }

    @Override
    public WallVisitor createWallVisitor() {
        return new MySqlWallVisitor();
    }

}
