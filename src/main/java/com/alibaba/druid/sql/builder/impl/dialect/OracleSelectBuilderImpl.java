package com.alibaba.druid.sql.builder.impl.dialect;

import com.alibaba.druid.sql.builder.impl.SQLSelectBuilderImpl;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleOrderBy;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelect;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.util.JdbcConstants;

public class OracleSelectBuilderImpl extends SQLSelectBuilderImpl {

    public OracleSelectBuilderImpl(){
        super(JdbcConstants.ORACLE);
    }

    protected OracleSelectQueryBlock createSelectQueryBlock() {
        return new OracleSelectQueryBlock();
    }

    protected OracleSelect createSelect() {
        return new OracleSelect();
    }
    
    protected OracleOrderBy createOrderBy() {
        return new OracleOrderBy();
    }
}
