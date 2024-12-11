package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLArgument;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatementImpl;

import java.util.ArrayList;
import java.util.List;

public class SQLExecuteImmediateStatement extends SQLStatementImpl {
    protected SQLExpr dynamicSql;

    protected final List<SQLArgument> arguments = new ArrayList<SQLArgument>();

    protected final List<SQLExpr> into = new ArrayList<SQLExpr>();
}
