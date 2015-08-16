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
package com.alibaba.druid.sql.dialect.mysql.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlOutFileExpr extends MySqlObjectImpl implements SQLExpr {

    private SQLExpr        file;
    private String         charset;

    private SQLLiteralExpr columnsTerminatedBy;
    private boolean        columnsEnclosedOptionally = false;
    private SQLLiteralExpr columnsEnclosedBy;
    private SQLLiteralExpr columnsEscaped;

    private SQLLiteralExpr linesStartingBy;
    private SQLLiteralExpr linesTerminatedBy;

    private SQLExpr        ignoreLinesNumber;

    public MySqlOutFileExpr(){
    }

    public MySqlOutFileExpr(SQLExpr file){
        this.file = file;
    }

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, file);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getFile() {
        return file;
    }

    public void setFile(SQLExpr file) {
        this.file = file;
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

}
