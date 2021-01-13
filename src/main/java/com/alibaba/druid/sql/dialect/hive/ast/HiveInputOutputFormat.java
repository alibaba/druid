package com.alibaba.druid.sql.dialect.hive.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class HiveInputOutputFormat extends SQLExprImpl {
    private SQLExpr input;
    private SQLExpr output;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HiveInputOutputFormat that = (HiveInputOutputFormat) o;

        if (input != null ? !input.equals(that.input) : that.input != null) return false;
        return output != null ? output.equals(that.output) : that.output == null;
    }

    @Override
    public int hashCode() {
        int result = input != null ? input.hashCode() : 0;
        result = 31 * result + (output != null ? output.hashCode() : 0);
        return result;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, input);
            acceptChild(v, output);
        }
        v.endVisit(this);
    }

    @Override
    public HiveInputOutputFormat clone() {
        HiveInputOutputFormat x = new HiveInputOutputFormat();

        if (input != null) {
            x.setInput(input.clone());
        }
        if (output != null) {
            x.setOutput(output.clone());
        }

        return x;
    }

    public SQLExpr getInput() {
        return input;
    }

    public void setInput(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.input = x;
    }

    public SQLExpr getOutput() {
        return output;
    }

    public void setOutput(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.output = x;
    }
}
