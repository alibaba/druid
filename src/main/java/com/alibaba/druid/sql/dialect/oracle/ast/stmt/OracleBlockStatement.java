package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleBlockStatement extends OracleStatementImpl {

    private static final long         serialVersionUID = 1L;

    private List<SQLColumnDefinition> parameters       = new ArrayList<SQLColumnDefinition>();

    private List<SQLStatement>        statementList    = new ArrayList<SQLStatement>();

    public List<SQLStatement> getStatementList() {
        return statementList;
    }

    public void setStatementList(List<SQLStatement> statementList) {
        this.statementList = statementList;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, parameters);
            acceptChild(visitor, statementList);
        }
        visitor.endVisit(this);
    }

    public List<SQLColumnDefinition> getParameters() {
        return parameters;
    }

    public void setParameters(List<SQLColumnDefinition> parameters) {
        this.parameters = parameters;
    }

}
