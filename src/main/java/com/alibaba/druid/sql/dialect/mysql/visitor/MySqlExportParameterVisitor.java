package com.alibaba.druid.sql.dialect.mysql.visitor;

import java.util.List;

import com.alibaba.druid.mapping.spi.ExportParameterVisitorUtils;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock.Limit;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;

public class MySqlExportParameterVisitor extends MySqlASTVisitorAdapter implements ExportParameterVisitor {

    private final List<Object> parameters;

    public MySqlExportParameterVisitor(List<Object> parameters){
        this.parameters = parameters;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    @Override
    public boolean visit(SQLSelectItem x) {
        return false;
    }

    @Override
    public boolean visit(Limit x) {
        return false;
    }

    @Override
    public boolean visit(SQLOrderBy x) {
        return false;
    }

    @Override
    public boolean visit(SQLSelectGroupByClause x) {
        return false;
    }

    @Override
    public boolean visit(SQLMethodInvokeExpr x) {
        ExportParameterVisitorUtils.exportParamterAndAccept(this.parameters, x.getParameters());

        return true;
    }

    @Override
    public boolean visit(SQLInListExpr x) {
        ExportParameterVisitorUtils.exportParamterAndAccept(this.parameters, x.getTargetList());

        return true;
    }

    @Override
    public boolean visit(SQLBetweenExpr x) {
        ExportParameterVisitorUtils.exportParameter(this.parameters, x);
        return true;
    }

    public boolean visit(SQLBinaryOpExpr x) {
        ExportParameterVisitorUtils.exportParameter(this.parameters, x);
        return true;
    }

}
