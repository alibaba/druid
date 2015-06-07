package com.alibaba.druid.sql.builder.impl.dialect;

import com.alibaba.druid.sql.builder.impl.SQLSelectBuilderImpl;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelect;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.util.JdbcConstants;

public class SQLServerSelectBuilderImpl extends SQLSelectBuilderImpl {

    public SQLServerSelectBuilderImpl(){
        super(JdbcConstants.SQL_SERVER);
    }

    protected SQLServerSelectQueryBlock createSelectQueryBlock() {
        return new SQLServerSelectQueryBlock();
    }
    
    protected SQLServerSelect createSelect() {
        return new SQLServerSelect();
    }
}
