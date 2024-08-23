package com.alibaba.druid.sql.dialect.athena.ast.stmt;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.athena.ast.AthenaObject;
import com.alibaba.druid.sql.dialect.athena.visitor.AthenaASTVisitor;
import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoCreateTableStatement;

import java.util.LinkedHashMap;
import java.util.Map;

public class AthenaCreateTableStatement extends PrestoCreateTableStatement implements AthenaObject {
    protected Map<String, SQLObject> serdeProperties = new LinkedHashMap<String, SQLObject>();

    @Override
    public void accept0(AthenaASTVisitor visitor) {
        if (visitor.visit(this)) {
            visitor.endVisit(this);
        }
    }

    public Map<String, SQLObject> getSerdeProperties() {
        return serdeProperties;
    }
}
