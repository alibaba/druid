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
package com.alibaba.druid.sql.dialect.oracle.ast.clause;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public abstract class FlashbackQueryClause extends OracleSQLObjectImpl {

    private Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static enum Type {
        SCN, TIMESTAMP
    }

    public static class VersionsFlashbackQueryClause extends FlashbackQueryClause {

        private SQLExpr begin;
        private SQLExpr end;

        public SQLExpr getBegin() {
            return begin;
        }

        public void setBegin(SQLExpr begin) {
            this.begin = begin;
        }

        public SQLExpr getEnd() {
            return end;
        }

        public void setEnd(SQLExpr end) {
            this.end = end;
        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, begin);
                acceptChild(visitor, end);
            }
            visitor.endVisit(this);
        }
    }

    public static class AsOfFlashbackQueryClause extends FlashbackQueryClause {

        private SQLExpr expr;

        public SQLExpr getExpr() {
            return expr;
        }

        public void setExpr(SQLExpr expr) {
            this.expr = expr;
        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, expr);
            }
            visitor.endVisit(this);
        }
    }

    public static class AsOfSnapshotClause extends FlashbackQueryClause {

        private SQLExpr expr;

        public SQLExpr getExpr() {
            return expr;
        }

        public void setExpr(SQLExpr expr) {
            this.expr = expr;
        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, expr);
            }
            visitor.endVisit(this);
        }
    }
}
