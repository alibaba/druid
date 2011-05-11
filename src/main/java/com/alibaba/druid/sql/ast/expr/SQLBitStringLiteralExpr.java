package com.alibaba.druid.sql.ast.expr;

import java.util.BitSet;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

/**
 * SQL-92
 * <p>
 * &ltbit string literal> ::= B &ltquote> [ &ltbit> ... ] &ltquote> [ {
 * &ltseparator> ... &ltquote> [ &ltbit> ... ] &ltquote> }... ]
 * </p>
 * 
 * @author WENSHAO
 */
public class SQLBitStringLiteralExpr extends SQLLiteralExpr {
    private static final long serialVersionUID = 1L;

    private BitSet value;

    public SQLBitStringLiteralExpr() {

    }

    public BitSet getValue() {
        return value;
    }

    public void setValue(BitSet value) {
        this.value = value;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

    public void output(StringBuffer buf) {
        buf.append("b'");
        for (int i = 0; i < value.length(); ++i) {
            buf.append(value);
        }
        buf.append("'");
    }
}
