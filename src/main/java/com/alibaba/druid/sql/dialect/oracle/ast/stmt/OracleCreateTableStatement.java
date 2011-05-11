package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;

@SuppressWarnings("serial")
public class OracleCreateTableStatement extends SQLCreateTableStatement {

    public OracleCreateTableStatement() {

    }

    @Override
    public void output(StringBuffer buf) {
        if (Type.GLOBAL_TEMPORARY.equals(this.type)) {
            buf.append("CREATE GLOBAL TEMPORARY TABLE ");
        } else if (Type.LOCAL_TEMPORARY.equals(this.type)) {
            buf.append("CREATE LOCAL TEMPORARY TABLE ");
        } else {
            buf.append("CREATE TABLE ");
        }

        this.name.output(buf);
        buf.append(" ");
        buf.append("(");
        for (int i = 0, size = tableElementList.size(); i < size; ++i) {
            if (i != 0) {
                buf.append(", ");
            }
            tableElementList.get(i).output(buf);
        }
        buf.append(")");
    }
}
