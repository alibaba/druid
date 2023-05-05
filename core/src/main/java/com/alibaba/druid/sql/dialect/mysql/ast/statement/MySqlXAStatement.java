package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.DruidRuntimeException;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlXAStatement extends MySqlStatementImpl {
    private XAType type;
    private SQLExpr id;

    public XAType getType() {
        return type;
    }

    public void setType(XAType type) {
        this.type = type;
    }

    public SQLExpr getId() {
        return id;
    }

    public void setId(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.id = x;
    }

    public void accept0(MySqlASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, id);
        }
        v.endVisit(this);
    }

    public enum XAType {
        START,
        BEGIN,
        END,
        PREPARE,
        COMMIT,
        ROLLBACK,
        RECOVER;

        public static XAType of(String typeStr) {
            if (typeStr == null || typeStr.isEmpty()) {
                return null;
            }

            switch (typeStr.toUpperCase()) {
                case "START":
                    return XAType.START;
                case "BEGIN":
                    return XAType.BEGIN;
                case "END":
                    return XAType.END;
                case "PREPARE":
                    return XAType.PREPARE;
                case "COMMIT":
                    return XAType.COMMIT;
                case "ROLLBACK":
                    return XAType.ROLLBACK;
                case "RECOVER":
                    return XAType.RECOVER;
                default:
                    throw new DruidRuntimeException("not support xa type " + typeStr);
            }
        }
    }
}
