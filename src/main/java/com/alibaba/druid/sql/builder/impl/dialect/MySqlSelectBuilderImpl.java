package com.alibaba.druid.sql.builder.impl.dialect;

import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.builder.impl.SQLSelectBuilderImpl;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectGroupBy;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.util.JdbcConstants;

public class MySqlSelectBuilderImpl extends SQLSelectBuilderImpl {

    public MySqlSelectBuilderImpl(){
        super(JdbcConstants.MYSQL);
    }

    protected SQLSelectQuery createSelectQueryBlock() {
        return new MySqlSelectQueryBlock();
    }
    
    protected SQLSelectGroupByClause createGroupBy() {
        return new MySqlSelectGroupBy();
    }
}
