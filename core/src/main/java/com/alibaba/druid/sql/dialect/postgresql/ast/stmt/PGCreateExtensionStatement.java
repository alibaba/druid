package com.alibaba.druid.sql.dialect.postgresql.ast.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class PGCreateExtensionStatement extends SQLStatementImpl implements PGSQLStatement {
    public PGCreateExtensionStatement() {
        super(DbType.postgresql);
    }

    private SQLName name;
    private boolean ifNotExists;
    private SQLName schema;
    private String version;
    private boolean cascade;

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }

    public boolean isIfNotExists() {
        return ifNotExists;
    }

    public void setIfNotExists(boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
    }

    public SQLName getSchema() {
        return schema;
    }

    public void setSchema(SQLName schema) {
        if (schema != null) {
            schema.setParent(this);
        }
        this.schema = schema;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isCascade() {
        return cascade;
    }

    public void setCascade(boolean cascade) {
        this.cascade = cascade;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PGASTVisitor) {
            accept0((PGASTVisitor) visitor);
        }
    }

    @Override
    public void accept0(PGASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, schema);
        }
        visitor.endVisit(this);
    }
}
