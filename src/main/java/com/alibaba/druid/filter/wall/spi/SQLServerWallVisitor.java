package com.alibaba.druid.filter.wall.spi;

import java.util.List;

import com.alibaba.druid.filter.wall.Violation;
import com.alibaba.druid.filter.wall.WallConfig;
import com.alibaba.druid.filter.wall.WallVisitor;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllExpr;
import com.alibaba.druid.sql.ast.expr.SQLAnyExpr;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBitStringLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.alibaba.druid.sql.ast.expr.SQLCaseExpr.Item;
import com.alibaba.druid.sql.ast.expr.SQLCastExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLCurrentOfCursorExpr;
import com.alibaba.druid.sql.ast.expr.SQLDateLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLDefaultExpr;
import com.alibaba.druid.sql.ast.expr.SQLExistsExpr;
import com.alibaba.druid.sql.ast.expr.SQLHexExpr;
import com.alibaba.druid.sql.ast.expr.SQLHexStringLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntervalLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLListExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNotExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLSomeExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.NotNullConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCallStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCommentStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.ast.statement.SQLTruncateStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLUniqueConstraint;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;


public class SQLServerWallVisitor implements WallVisitor {

    private final WallConfig      config;
    
    /**
     * @param config
     */
    public SQLServerWallVisitor(WallConfig config) {
        this.config = config;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr)
     */
    @Override
    public void endVisit(SQLAllColumnExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLBetweenExpr)
     */
    @Override
    public void endVisit(SQLBetweenExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr)
     */
    @Override
    public void endVisit(SQLBinaryOpExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLCaseExpr)
     */
    @Override
    public void endVisit(SQLCaseExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLCaseExpr.Item)
     */
    @Override
    public void endVisit(Item x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLCharExpr)
     */
    @Override
    public void endVisit(SQLCharExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr)
     */
    @Override
    public void endVisit(SQLIdentifierExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLInListExpr)
     */
    @Override
    public void endVisit(SQLInListExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLIntegerExpr)
     */
    @Override
    public void endVisit(SQLIntegerExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLExistsExpr)
     */
    @Override
    public void endVisit(SQLExistsExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLNCharExpr)
     */
    @Override
    public void endVisit(SQLNCharExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLNotExpr)
     */
    @Override
    public void endVisit(SQLNotExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLNullExpr)
     */
    @Override
    public void endVisit(SQLNullExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLNumberExpr)
     */
    @Override
    public void endVisit(SQLNumberExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLPropertyExpr)
     */
    @Override
    public void endVisit(SQLPropertyExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause)
     */
    @Override
    public void endVisit(SQLSelectGroupByClause x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLSelectItem)
     */
    @Override
    public void endVisit(SQLSelectItem x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLSelectStatement)
     */
    @Override
    public void endVisit(SQLSelectStatement selectStatement) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#postVisit(com.alibaba.druid.sql.ast.SQLObject)
     */
    @Override
    public void postVisit(SQLObject astNode) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#preVisit(com.alibaba.druid.sql.ast.SQLObject)
     */
    @Override
    public void preVisit(SQLObject astNode) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr)
     */
    @Override
    public boolean visit(SQLAllColumnExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLBetweenExpr)
     */
    @Override
    public boolean visit(SQLBetweenExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr)
     */
    @Override
    public boolean visit(SQLBinaryOpExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLCaseExpr)
     */
    @Override
    public boolean visit(SQLCaseExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLCaseExpr.Item)
     */
    @Override
    public boolean visit(Item x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLCastExpr)
     */
    @Override
    public boolean visit(SQLCastExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLCharExpr)
     */
    @Override
    public boolean visit(SQLCharExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLExistsExpr)
     */
    @Override
    public boolean visit(SQLExistsExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr)
     */
    @Override
    public boolean visit(SQLIdentifierExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLInListExpr)
     */
    @Override
    public boolean visit(SQLInListExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLIntegerExpr)
     */
    @Override
    public boolean visit(SQLIntegerExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLNCharExpr)
     */
    @Override
    public boolean visit(SQLNCharExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLNotExpr)
     */
    @Override
    public boolean visit(SQLNotExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLNullExpr)
     */
    @Override
    public boolean visit(SQLNullExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLNumberExpr)
     */
    @Override
    public boolean visit(SQLNumberExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLPropertyExpr)
     */
    @Override
    public boolean visit(SQLPropertyExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause)
     */
    @Override
    public boolean visit(SQLSelectGroupByClause x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLSelectItem)
     */
    @Override
    public boolean visit(SQLSelectItem x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLCastExpr)
     */
    @Override
    public void endVisit(SQLCastExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLSelectStatement)
     */
    @Override
    public boolean visit(SQLSelectStatement astNode) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLAggregateExpr)
     */
    @Override
    public void endVisit(SQLAggregateExpr astNode) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLAggregateExpr)
     */
    @Override
    public boolean visit(SQLAggregateExpr astNode) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr)
     */
    @Override
    public boolean visit(SQLVariantRefExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr)
     */
    @Override
    public void endVisit(SQLVariantRefExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLQueryExpr)
     */
    @Override
    public boolean visit(SQLQueryExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLQueryExpr)
     */
    @Override
    public void endVisit(SQLQueryExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLUnaryExpr)
     */
    @Override
    public boolean visit(SQLUnaryExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLUnaryExpr)
     */
    @Override
    public void endVisit(SQLUnaryExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLHexExpr)
     */
    @Override
    public boolean visit(SQLHexExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLHexExpr)
     */
    @Override
    public void endVisit(SQLHexExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLBitStringLiteralExpr)
     */
    @Override
    public boolean visit(SQLBitStringLiteralExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLBitStringLiteralExpr)
     */
    @Override
    public void endVisit(SQLBitStringLiteralExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLHexStringLiteralExpr)
     */
    @Override
    public boolean visit(SQLHexStringLiteralExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLHexStringLiteralExpr)
     */
    @Override
    public void endVisit(SQLHexStringLiteralExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLDateLiteralExpr)
     */
    @Override
    public boolean visit(SQLDateLiteralExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLDateLiteralExpr)
     */
    @Override
    public void endVisit(SQLDateLiteralExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLSelect)
     */
    @Override
    public boolean visit(SQLSelect x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLSelect)
     */
    @Override
    public void endVisit(SQLSelect select) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock)
     */
    @Override
    public boolean visit(SQLSelectQueryBlock x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock)
     */
    @Override
    public void endVisit(SQLSelectQueryBlock x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLExprTableSource)
     */
    @Override
    public boolean visit(SQLExprTableSource x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLExprTableSource)
     */
    @Override
    public void endVisit(SQLExprTableSource x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLIntervalLiteralExpr)
     */
    @Override
    public boolean visit(SQLIntervalLiteralExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLIntervalLiteralExpr)
     */
    @Override
    public void endVisit(SQLIntervalLiteralExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.SQLOrderBy)
     */
    @Override
    public boolean visit(SQLOrderBy x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.SQLOrderBy)
     */
    @Override
    public void endVisit(SQLOrderBy x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem)
     */
    @Override
    public boolean visit(SQLSelectOrderByItem x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem)
     */
    @Override
    public void endVisit(SQLSelectOrderByItem x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLDropTableStatement)
     */
    @Override
    public boolean visit(SQLDropTableStatement x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLDropTableStatement)
     */
    @Override
    public void endVisit(SQLDropTableStatement x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement)
     */
    @Override
    public boolean visit(SQLCreateTableStatement x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement)
     */
    @Override
    public void endVisit(SQLCreateTableStatement x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLTableElement)
     */
    @Override
    public boolean visit(SQLTableElement x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLTableElement)
     */
    @Override
    public void endVisit(SQLTableElement x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLColumnDefinition)
     */
    @Override
    public boolean visit(SQLColumnDefinition x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLColumnDefinition)
     */
    @Override
    public void endVisit(SQLColumnDefinition x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.SQLDataType)
     */
    @Override
    public boolean visit(SQLDataType x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.SQLDataType)
     */
    @Override
    public void endVisit(SQLDataType x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLDeleteStatement)
     */
    @Override
    public boolean visit(SQLDeleteStatement x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLDeleteStatement)
     */
    @Override
    public void endVisit(SQLDeleteStatement x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLCurrentOfCursorExpr)
     */
    @Override
    public boolean visit(SQLCurrentOfCursorExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLCurrentOfCursorExpr)
     */
    @Override
    public void endVisit(SQLCurrentOfCursorExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLInsertStatement)
     */
    @Override
    public boolean visit(SQLInsertStatement x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLInsertStatement)
     */
    @Override
    public void endVisit(SQLInsertStatement x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause)
     */
    @Override
    public boolean visit(ValuesClause x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause)
     */
    @Override
    public void endVisit(ValuesClause x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem)
     */
    @Override
    public boolean visit(SQLUpdateSetItem x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem)
     */
    @Override
    public void endVisit(SQLUpdateSetItem x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLUpdateStatement)
     */
    @Override
    public boolean visit(SQLUpdateStatement x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLUpdateStatement)
     */
    @Override
    public void endVisit(SQLUpdateStatement x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement)
     */
    @Override
    public boolean visit(SQLCreateViewStatement x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement)
     */
    @Override
    public void endVisit(SQLCreateViewStatement x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLUniqueConstraint)
     */
    @Override
    public boolean visit(SQLUniqueConstraint x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLUniqueConstraint)
     */
    @Override
    public void endVisit(SQLUniqueConstraint x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.NotNullConstraint)
     */
    @Override
    public boolean visit(NotNullConstraint x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.NotNullConstraint)
     */
    @Override
    public void endVisit(NotNullConstraint x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr)
     */
    @Override
    public void endVisit(SQLMethodInvokeExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr)
     */
    @Override
    public boolean visit(SQLMethodInvokeExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLUnionQuery)
     */
    @Override
    public void endVisit(SQLUnionQuery x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLUnionQuery)
     */
    @Override
    public boolean visit(SQLUnionQuery x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLSetStatement)
     */
    @Override
    public void endVisit(SQLSetStatement x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLSetStatement)
     */
    @Override
    public boolean visit(SQLSetStatement x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLAssignItem)
     */
    @Override
    public void endVisit(SQLAssignItem x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLAssignItem)
     */
    @Override
    public boolean visit(SQLAssignItem x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLCallStatement)
     */
    @Override
    public void endVisit(SQLCallStatement x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLCallStatement)
     */
    @Override
    public boolean visit(SQLCallStatement x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLJoinTableSource)
     */
    @Override
    public void endVisit(SQLJoinTableSource x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLJoinTableSource)
     */
    @Override
    public boolean visit(SQLJoinTableSource x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLSomeExpr)
     */
    @Override
    public void endVisit(SQLSomeExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLSomeExpr)
     */
    @Override
    public boolean visit(SQLSomeExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLAnyExpr)
     */
    @Override
    public void endVisit(SQLAnyExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLAnyExpr)
     */
    @Override
    public boolean visit(SQLAnyExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLAllExpr)
     */
    @Override
    public void endVisit(SQLAllExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLAllExpr)
     */
    @Override
    public boolean visit(SQLAllExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr)
     */
    @Override
    public void endVisit(SQLInSubQueryExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr)
     */
    @Override
    public boolean visit(SQLInSubQueryExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLListExpr)
     */
    @Override
    public void endVisit(SQLListExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLListExpr)
     */
    @Override
    public boolean visit(SQLListExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource)
     */
    @Override
    public void endVisit(SQLSubqueryTableSource x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource)
     */
    @Override
    public boolean visit(SQLSubqueryTableSource x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLTruncateStatement)
     */
    @Override
    public void endVisit(SQLTruncateStatement x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLTruncateStatement)
     */
    @Override
    public boolean visit(SQLTruncateStatement x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.expr.SQLDefaultExpr)
     */
    @Override
    public void endVisit(SQLDefaultExpr x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.expr.SQLDefaultExpr)
     */
    @Override
    public boolean visit(SQLDefaultExpr x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#endVisit(com.alibaba.druid.sql.ast.statement.SQLCommentStatement)
     */
    @Override
    public void endVisit(SQLCommentStatement x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.sql.visitor.SQLASTVisitor#visit(com.alibaba.druid.sql.ast.statement.SQLCommentStatement)
     */
    @Override
    public boolean visit(SQLCommentStatement x) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.filter.wall.WallVisitor#getConfig()
     */
    @Override
    public WallConfig getConfig() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.filter.wall.WallVisitor#getViolations()
     */
    @Override
    public List<Violation> getViolations() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.filter.wall.WallVisitor#isPermitTable(java.lang.String)
     */
    @Override
    public boolean isPermitTable(String name) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.filter.wall.WallVisitor#toSQL(com.alibaba.druid.sql.ast.SQLObject)
     */
    @Override
    public String toSQL(SQLObject obj) {
        // TODO Auto-generated method stub
        return null;
    }

}
