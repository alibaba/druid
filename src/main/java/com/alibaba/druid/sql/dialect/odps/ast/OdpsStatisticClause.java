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
package com.alibaba.druid.sql.dialect.odps.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;

public abstract class OdpsStatisticClause extends OdpsObjectImpl {

    public static abstract class ColumnStatisticClause extends OdpsStatisticClause {
    
        protected SQLName column;
    
        public SQLName getColumn() {
            return column;
        }
    
        public void setColumn(SQLName column) {
            if (column != null) {
                column.setParent(this);
            }
            this.column = column;
        }
    
    }

    public static class NullValue extends ColumnStatisticClause {
        @Override
        public void accept0(OdpsASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, column);
            }
            visitor.endVisit(this);
        }
    }

    public static class ColumnSum extends ColumnStatisticClause {
        @Override
        public void accept0(OdpsASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, column);
            }
            visitor.endVisit(this);
        }
    }

    public static class ColumnMin extends ColumnStatisticClause {
        @Override
        public void accept0(OdpsASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, column);
            }
            visitor.endVisit(this);
        }
    }

    public static class ColumnMax extends ColumnStatisticClause {
        @Override
        public void accept0(OdpsASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, column);
            }
            visitor.endVisit(this);
        }
    }

    public static class ExpressionCondition extends OdpsStatisticClause {
    
        private SQLExpr expr;
    
        public SQLExpr getExpr() {
            return expr;
        }
    
        public void setExpr(SQLExpr expr) {
            if (expr != null) {
                expr.setParent(this);
            }
            this.expr = expr;
        }
    
        @Override
        public void accept0(OdpsASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, expr);
            }
            visitor.endVisit(this);
        }
    }

    public static class TableCount extends OdpsStatisticClause {
    
        @Override
        public void accept0(OdpsASTVisitor visitor) {
            if (visitor.visit(this)) {
    
            }
            visitor.endVisit(this);
        }
    
    }

}