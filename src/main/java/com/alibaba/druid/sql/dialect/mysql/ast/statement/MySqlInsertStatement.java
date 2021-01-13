/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class MySqlInsertStatement extends SQLInsertStatement {
    private boolean             lowPriority        = false;
    private boolean             delayed            = false;
    private boolean             highPriority       = false;
    private boolean             ignore             = false;
    private boolean             rollbackOnFail     = false;
    private boolean             fulltextDictionary     = false; // for adb
    private boolean             overwrite     = false; // for adb
    private boolean             ifNotExists   = false; //for adb

    protected List<SQLCommentHint>      hints;

    private final List<SQLExpr> duplicateKeyUpdate = new ArrayList<SQLExpr>();

    public MySqlInsertStatement() {
        dbType = DbType.mysql;
    }

    public void cloneTo(MySqlInsertStatement x) {
        super.cloneTo(x);
        x.lowPriority = lowPriority;
        x.delayed = delayed;
        x.highPriority = highPriority;
        x.ignore = ignore;
        x.rollbackOnFail = rollbackOnFail;
        x.fulltextDictionary = fulltextDictionary;
        x.overwrite = overwrite;
        x.ifNotExists = ifNotExists;

        for (SQLExpr e : duplicateKeyUpdate) {
            SQLExpr e2 = e.clone();
            e2.setParent(x);
            x.duplicateKeyUpdate.add(e2);
        }
    }

    public List<SQLExpr> getDuplicateKeyUpdate() {
        return duplicateKeyUpdate;
    }

    public boolean isLowPriority() {
        return lowPriority;
    }

    public void setLowPriority(boolean lowPriority) {
        this.lowPriority = lowPriority;
    }

    public boolean isDelayed() {
        return delayed;
    }

    public void setDelayed(boolean delayed) {
        this.delayed = delayed;
    }

    public boolean isHighPriority() {
        return highPriority;
    }

    public void setHighPriority(boolean highPriority) {
        this.highPriority = highPriority;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public boolean isRollbackOnFail() {
        return rollbackOnFail;
    }

    public void setRollbackOnFail(boolean rollbackOnFail) {
        this.rollbackOnFail = rollbackOnFail;
    }

    public boolean isFulltextDictionary() {
        return fulltextDictionary;
    }

    public void setFulltextDictionary(boolean fulltextDictionary) {
        this.fulltextDictionary = fulltextDictionary;
    }

    public boolean isIfNotExists() {
        return ifNotExists;
    }

    public void setIfNotExists(boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof MySqlASTVisitor) {
            accept0((MySqlASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    protected void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (tableSource != null) {
                tableSource.accept(visitor);
            }

            if (columns != null) {
                for (SQLExpr column : columns) {
                    if (column != null) {
                        column.accept(visitor);
                    }
                }
            }

            if (valuesList != null) {
                for (ValuesClause values : valuesList) {
                    if (values != null) {
                        values.accept(visitor);
                    }
                }
            }

            if (query != null) {
                query.accept(visitor);
            }

            if (duplicateKeyUpdate != null) {
                for (SQLExpr item : duplicateKeyUpdate) {
                    if (item != null) {
                        item.accept(visitor);
                    }
                }
            }
        }

        visitor.endVisit(this);
    }

    public int getHintsSize() {
        if (hints == null) {
            return 0;
        }
        return hints.size();
    }

    public List<SQLCommentHint> getHints() {
        return hints;
    }

    public void setHints(List<SQLCommentHint> x) {
        if (x != null) {
            for (int i = 0; i < x.size(); i++) {
                x.get(i).setParent(this);
            }
        }
        this.hints = x;
    }

    public SQLInsertStatement clone() {
        MySqlInsertStatement x = new MySqlInsertStatement();
        cloneTo(x);
        return x;
    }

    @Override
    public boolean isOverwrite() {
        return overwrite;
    }

    @Override
    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }
}
