package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

@SuppressWarnings("serial")
public class SQLCreateTableStatement extends SQLStatementImpl {

    protected Type type;
    protected SQLName name;

    protected List<SQLTableElement> tableElementList = new ArrayList<SQLTableElement>();

    public SQLCreateTableStatement() {

    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static enum Type {
        GLOBAL_TEMPORARY,
        LOCAL_TEMPORARY
    }

    public List<SQLTableElement> getTableElementList() {
        return tableElementList;
    }

    @Override
    public void output(StringBuffer buf) {
        buf.append("CREATE TABLE ");
        if (Type.GLOBAL_TEMPORARY.equals(this.type)) {
            buf.append("GLOBAL TEMPORARY ");
        } else if (Type.LOCAL_TEMPORARY.equals(this.type)) {
            buf.append("LOCAL TEMPORARY ");
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

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, name);
            this.acceptChild(visitor, tableElementList);
        }
        visitor.endVisit(this);
    }
}
