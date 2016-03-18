package com.alibaba.druid.sql.dialect.teradata.visitor;

import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcUtils;

public class TeradataSchemaStatVisitor extends SchemaStatVisitor implements TeradataASTVisitor {

    @Override
    public String getDbType() {
        return JdbcUtils.POSTGRESQL;
    }

}
