package com.alibaba.druid.sql.builder.impl.dialect;

import com.alibaba.druid.sql.builder.impl.SQLSelectBuilderImpl;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGOrderBy;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

public class PGSelectBuilderImpl extends SQLSelectBuilderImpl {

    public PGSelectBuilderImpl(){
        super(new PGSelectStatement(), JdbcConstants.POSTGRESQL);
    }

    protected PGSelectQueryBlock createSelectQueryBlock() {
        return new PGSelectQueryBlock();
    }
    
    protected PGOrderBy createOrderBy() {
        return new PGOrderBy();
    }
}
