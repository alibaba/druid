package com.alibaba.druid.sql.dialect.sqlserver.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerObject;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLServerInsertStatement extends SQLInsertStatement implements SQLServerObject {

    private static final long  serialVersionUID = 2652393792927901563L;

    private List<ValuesClause> valuesList       = new ArrayList<ValuesClause>();

    public ValuesClause getValues() {
        if (valuesList.size() == 0) {
            return null;
        }
        return valuesList.get(0);
    }

    public void setValues(ValuesClause values) {
        if (valuesList.size() == 0) {
            valuesList.add(values);
        } else {
            valuesList.set(0, values);
        }
    }

    public List<ValuesClause> getValuesList() {
        return valuesList;
    }
    
    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((SQLServerASTVisitor) visitor);
    }

    @Override
    public void accept0(SQLServerASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, getTableSource());
            this.acceptChild(visitor, getColumns());
            this.acceptChild(visitor, getValuesList());
            this.acceptChild(visitor, getQuery());
        }

        visitor.endVisit(this);
    }
}
