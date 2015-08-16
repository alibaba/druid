/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlLoadDataInFileStatement extends MySqlStatementImpl {

    private boolean             lowPriority               = false;
    private boolean             concurrent                = false;
    private boolean             local                     = false;

    private SQLLiteralExpr      fileName;

    private boolean             replicate                 = false;
    private boolean             ignore                    = false;

    private SQLName             tableName;

    private String              charset;

    private SQLLiteralExpr      columnsTerminatedBy;
    private boolean             columnsEnclosedOptionally = false;
    private SQLLiteralExpr      columnsEnclosedBy;
    private SQLLiteralExpr      columnsEscaped;

    private SQLLiteralExpr      linesStartingBy;
    private SQLLiteralExpr      linesTerminatedBy;

    private SQLExpr             ignoreLinesNumber;

    private List<SQLExpr>  setList                   = new ArrayList<SQLExpr>();

    private List<SQLExpr>  columns                   = new ArrayList<SQLExpr>();

    public boolean isLowPriority() {
        return lowPriority;
    }

    public void setLowPriority(boolean lowPriority) {
        this.lowPriority = lowPriority;
    }

    public boolean isConcurrent() {
        return concurrent;
    }

    public void setConcurrent(boolean concurrent) {
        this.concurrent = concurrent;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public SQLLiteralExpr getFileName() {
        return fileName;
    }

    public void setFileName(SQLLiteralExpr fileName) {
        this.fileName = fileName;
    }

    public boolean isReplicate() {
        return replicate;
    }

    public void setReplicate(boolean replicate) {
        this.replicate = replicate;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public SQLName getTableName() {
        return tableName;
    }

    public void setTableName(SQLName tableName) {
        this.tableName = tableName;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public SQLLiteralExpr getColumnsTerminatedBy() {
        return columnsTerminatedBy;
    }

    public void setColumnsTerminatedBy(SQLLiteralExpr columnsTerminatedBy) {
        this.columnsTerminatedBy = columnsTerminatedBy;
    }

    public boolean isColumnsEnclosedOptionally() {
        return columnsEnclosedOptionally;
    }

    public void setColumnsEnclosedOptionally(boolean columnsEnclosedOptionally) {
        this.columnsEnclosedOptionally = columnsEnclosedOptionally;
    }

    public SQLLiteralExpr getColumnsEnclosedBy() {
        return columnsEnclosedBy;
    }

    public void setColumnsEnclosedBy(SQLLiteralExpr columnsEnclosedBy) {
        this.columnsEnclosedBy = columnsEnclosedBy;
    }

    public SQLLiteralExpr getColumnsEscaped() {
        return columnsEscaped;
    }

    public void setColumnsEscaped(SQLLiteralExpr columnsEscaped) {
        this.columnsEscaped = columnsEscaped;
    }

    public SQLLiteralExpr getLinesStartingBy() {
        return linesStartingBy;
    }

    public void setLinesStartingBy(SQLLiteralExpr linesStartingBy) {
        this.linesStartingBy = linesStartingBy;
    }

    public SQLLiteralExpr getLinesTerminatedBy() {
        return linesTerminatedBy;
    }

    public void setLinesTerminatedBy(SQLLiteralExpr linesTerminatedBy) {
        this.linesTerminatedBy = linesTerminatedBy;
    }

    public SQLExpr getIgnoreLinesNumber() {
        return ignoreLinesNumber;
    }

    public void setIgnoreLinesNumber(SQLExpr ignoreLinesNumber) {
        this.ignoreLinesNumber = ignoreLinesNumber;
    }

    public List<SQLExpr> getSetList() {
        return setList;
    }

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, fileName);
            acceptChild(visitor, tableName);
            acceptChild(visitor, columnsTerminatedBy);
            acceptChild(visitor, columnsEnclosedBy);
            acceptChild(visitor, columnsEscaped);
            acceptChild(visitor, linesStartingBy);
            acceptChild(visitor, linesTerminatedBy);
            acceptChild(visitor, ignoreLinesNumber);
            acceptChild(visitor, setList);
        }
        visitor.endVisit(this);
    }

    
    public List<SQLExpr> getColumns() {
        return columns;
    }

    
    public void setColumns(List<SQLExpr> columns) {
        this.columns = columns;
    }

    
    public void setSetList(List<SQLExpr> setList) {
        this.setList = setList;
    }
}
