/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObject;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

public class SQLSelect extends SQLObjectImpl {

    protected SQLWithSubqueryClause withSubQuery;
    protected SQLSelectQuery        query;
    protected SQLOrderBy            orderBy;
    protected SQLLimit              limit;

    protected List<SQLHint>         hints;

    protected SQLObject             restriction;

    protected boolean               forBrowse;
    protected List<String>          forXmlOptions = null;
    protected SQLExpr               xmlPath;

    protected SQLExpr                rowCount;
    protected SQLExpr                offset;

    public SQLSelect(){

    }

    public List<SQLHint> getHints() {
        if (hints == null) {
            hints = new ArrayList<SQLHint>(2);
        }
        return hints;
    }
    
    public int getHintsSize() {
        if (hints == null) {
            return 0;
        }
        return hints.size();
    }

    public SQLSelect(SQLSelectQuery query){
        this.setQuery(query);
    }

    public SQLWithSubqueryClause getWithSubQuery() {
        return withSubQuery;
    }

    public void setWithSubQuery(SQLWithSubqueryClause withSubQuery) {
        this.withSubQuery = withSubQuery;
    }

    public SQLSelectQuery getQuery() {
        return this.query;
    }

    public void setQuery(SQLSelectQuery query) {
        if (query != null) {
            query.setParent(this);
        }
        this.query = query;
    }

    public SQLSelectQueryBlock getQueryBlock() {
        if (query instanceof SQLSelectQueryBlock) {
            return (SQLSelectQueryBlock) query;
        }

        return null;
    }

    public SQLOrderBy getOrderBy() {
        return this.orderBy;
    }

    public void setOrderBy(SQLOrderBy orderBy) {
        if (orderBy != null) {
            orderBy.setParent(this);
        }
        this.orderBy = orderBy;
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.withSubQuery);
            acceptChild(visitor, this.query);
            acceptChild(visitor, this.restriction);
            acceptChild(visitor, this.orderBy);
            acceptChild(visitor, this.hints);
            acceptChild(visitor, this.offset);
            acceptChild(visitor, this.rowCount);
        }

        visitor.endVisit(this);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SQLSelect sqlSelect = (SQLSelect) o;

        if (forBrowse != sqlSelect.forBrowse) {
            return false;
        }
        if (withSubQuery != null ? !withSubQuery.equals(sqlSelect.withSubQuery) : sqlSelect.withSubQuery != null) {
            return false;
        }
        if (query != null ? !query.equals(sqlSelect.query) : sqlSelect.query != null) {
            return false;
        }
        if (orderBy != null ? !orderBy.equals(sqlSelect.orderBy) : sqlSelect.orderBy != null) {
            return false;
        }
        if (limit != null ? !limit.equals(sqlSelect.limit) : sqlSelect.limit != null) {
            return false;
        }
        if (hints != null ? !hints.equals(sqlSelect.hints) : sqlSelect.hints != null) {
            return false;
        }
        if (restriction != null ? !restriction.equals(sqlSelect.restriction) : sqlSelect.restriction != null) {
            return false;
        }
        if (forXmlOptions != null ? !forXmlOptions.equals(sqlSelect.forXmlOptions) : sqlSelect.forXmlOptions != null) {
            return false;
        }
        if (xmlPath != null ? !xmlPath.equals(sqlSelect.xmlPath) : sqlSelect.xmlPath != null) {
            return false;
        }
        if (rowCount != null ? !rowCount.equals(sqlSelect.rowCount) : sqlSelect.rowCount != null) {
            return false;
        }
        return offset != null ? offset.equals(sqlSelect.offset) : sqlSelect.offset == null;
    }

    @Override
    public int hashCode()
    {
        int result = withSubQuery != null ? withSubQuery.hashCode() : 0;
        result = 31 * result + (query != null ? query.hashCode() : 0);
        result = 31 * result + (orderBy != null ? orderBy.hashCode() : 0);
        result = 31 * result + (limit != null ? limit.hashCode() : 0);
        result = 31 * result + (hints != null ? hints.hashCode() : 0);
        result = 31 * result + (restriction != null ? restriction.hashCode() : 0);
        result = 31 * result + (forBrowse ? 1 : 0);
        result = 31 * result + (forXmlOptions != null ? forXmlOptions.hashCode() : 0);
        result = 31 * result + (xmlPath != null ? xmlPath.hashCode() : 0);
        result = 31 * result + (rowCount != null ? rowCount.hashCode() : 0);
        result = 31 * result + (offset != null ? offset.hashCode() : 0);
        return result;
    }

    public void output(StringBuffer buf) {
        String dbType = null;

        SQLObject parent = this.getParent();
        if (parent instanceof SQLStatement) {
            dbType = ((SQLStatement) parent).getDbType();
        }

        if (dbType == null && parent instanceof OracleSQLObject) {
            dbType = JdbcConstants.ORACLE;
        }

        if (dbType == null && query instanceof SQLSelectQueryBlock) {
            dbType = ((SQLSelectQueryBlock) query).dbType;
        }

        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(buf, dbType);
        this.accept(visitor);
    }

    public String toString() {
        SQLObject parent = this.getParent();
        if (parent instanceof SQLStatement) {
            String dbType = ((SQLStatement) parent).getDbType();
            
            if (dbType != null) {
                return SQLUtils.toSQLString(this, dbType);
            }
        }

        if (parent instanceof OracleSQLObject) {
            return SQLUtils.toSQLString(this, JdbcConstants.ORACLE);
        }

        if (query instanceof SQLSelectQueryBlock) {
            String dbType = ((SQLSelectQueryBlock) query).dbType;

            if (dbType != null) {
                return SQLUtils.toSQLString(this, dbType);
            }
        }
        
        return super.toString();
    }

    public SQLSelect clone() {
        SQLSelect x = new SQLSelect();

        x.withSubQuery = this.withSubQuery;
        if (query != null) {
            x.setQuery(query.clone());
        }

        if (orderBy != null) {
            x.setOrderBy(this.orderBy.clone());
        }
        if (restriction != null) {
            x.setRestriction(restriction.clone());
        }

        if (this.hints != null) {
            for (SQLHint hint : this.hints) {
                x.hints.add(hint);
            }
        }

        x.forBrowse = forBrowse;

        if (forXmlOptions != null) {
            x.forXmlOptions = new ArrayList<String>(forXmlOptions);
        }

        if (xmlPath != null) {
            x.setXmlPath(xmlPath.clone());
        }

        if (rowCount != null) {
            x.setRowCount(rowCount.clone());
        }

        if (offset != null) {
            x.setOffset(offset.clone());
        }

        return x;
    }

    public boolean isSimple() {
        return withSubQuery == null
                && (hints == null || hints.size() == 0)
                && restriction == null
                && (!forBrowse)
                && (forXmlOptions == null || forXmlOptions.size() == 0)
                && xmlPath == null
                && rowCount == null
                && offset == null;
    }

    public SQLObject getRestriction() {
        return this.restriction;
    }

    public void setRestriction(SQLObject restriction) {
        if (restriction != null) {
            restriction.setParent(this);
        }
        this.restriction = restriction;
    }

    public boolean isForBrowse() {
        return forBrowse;
    }

    public void setForBrowse(boolean forBrowse) {
        this.forBrowse = forBrowse;
    }

    public List<String> getForXmlOptions() {
        if (forXmlOptions == null) {
            forXmlOptions = new ArrayList<String>(4);
        }

        return forXmlOptions;
    }

    public int getForXmlOptionsSize() {
        if (forXmlOptions == null) {
            return 0;
        }
        return forXmlOptions.size();
    }

    public SQLExpr getRowCount() {
        return rowCount;
    }

    public void setRowCount(SQLExpr rowCount) {
        if (rowCount != null) {
            rowCount.setParent(this);
        }

        this.rowCount = rowCount;
    }

    public SQLExpr getOffset() {
        return offset;
    }

    public void setOffset(SQLExpr offset) {
        if (offset != null) {
            offset.setParent(this);
        }
        this.offset = offset;
    }

    public SQLExpr getXmlPath() {
        return xmlPath;
    }

    public void setXmlPath(SQLExpr xmlPath) {
        if (xmlPath != null) {
            xmlPath.setParent(this);
        }
        this.xmlPath = xmlPath;
    }

    public SQLSelectQueryBlock getFirstQueryBlock() {
        if (query instanceof SQLSelectQueryBlock) {
            return (SQLSelectQueryBlock) query;
        }

        if (query instanceof SQLUnionQuery) {
            return ((SQLUnionQuery) query).getFirstQueryBlock();
        }

        return null;
    }

    public boolean addWhere(SQLExpr where) {
        if (where == null) {
            return false;
        }

        if (query instanceof SQLSelectQueryBlock) {
            ((SQLSelectQueryBlock) query).addWhere(where);
            return true;
        }

        if (query instanceof SQLUnionQuery) {
            SQLSelectQueryBlock queryBlock = new SQLSelectQueryBlock();
            queryBlock.setFrom(new SQLSelect(query), "u");
            queryBlock.addSelectItem(new SQLAllColumnExpr());
            queryBlock.setParent(queryBlock);
            query = queryBlock;
            return true;
        }

        return false;
    }

    public SQLLimit getLimit() {
        return limit;
    }

    public void setLimit(SQLLimit x) {
        if (x != null) {
            x.setParent(this);
        }
        this.limit = x;
    }
}
