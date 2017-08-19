package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleCreateSynonymStatement extends OracleStatementImpl implements SQLCreateStatement {
    private boolean orReplace;
    private SQLName name;
    private boolean isPublic;
    private SQLName object;

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, object);
        }
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean value) {
        isPublic = value;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }

    public SQLName getObject() {
        return object;
    }

    public void setObject(SQLName object) {
        if (object != null) {
            object.setParent(this);
        }
        this.object = object;
    }

    public boolean isOrReplace() {
        return orReplace;
    }

    public void setOrReplace(boolean orReplace) {
        this.orReplace = orReplace;
    }
}
