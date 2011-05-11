package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObject;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;

public abstract class OracleSelectRestriction extends OracleSQLObject {
    private static final long serialVersionUID = 1L;

    public OracleSelectRestriction() {

    }

    public static class CheckOption extends OracleSelectRestriction {
        private static final long serialVersionUID = 1L;

        private OracleConstraint constraint;

        public CheckOption() {

        }

        public OracleConstraint getConstraint() {
            return this.constraint;
        }

        public void setConstraint(OracleConstraint constraint) {
            this.constraint = constraint;
        }

        protected void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.constraint);
            }

            visitor.endVisit(this);
        }
    }

    public static class ReadOnly extends OracleSelectRestriction {
        private static final long serialVersionUID = 1L;

        public ReadOnly() {

        }

        protected void accept0(OracleASTVisitor visitor) {
            visitor.visit(this);

            visitor.endVisit(this);
        }
    }
}
