package com.alibaba.druid.sql.dialect.mysql.ast.expr;

import com.alibaba.druid.sql.ast.expr.SQLCharExpr;

public class MySqlCharExpr extends SQLCharExpr implements MySqlExpr {
    private static final long serialVersionUID = 1L;

    private String charset;
    private String collate;

    public MySqlCharExpr() {

    }

    public MySqlCharExpr(String text) {
        super(text);
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getCollate() {
        return collate;
    }

    public void setCollate(String collate) {
        this.collate = collate;
    }

    public void output(StringBuffer buf) {
        if (charset != null) {
            buf.append(charset);
            buf.append(' ');
        }

        super.output(buf);

        if (collate != null) {
            buf.append(" COLLATE ");
            buf.append(collate);
        }
    }
}
