package com.alibaba.druid.sql.dialect.sqlserver.ast.expr;

import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerObjectImpl;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;

public class SQLServerObjectReferenceExpr extends SQLServerObjectImpl implements SQLServerExpr {

    private static final long serialVersionUID = 1L;

    private String            server;
    private String            database;
    private String            schema;
    private String            object;

    @Override
    public void accept0(SQLServerASTVisitor visitor) {

    }

    public void output(StringBuffer buf) {
        boolean flag = false;
        if (server != null) {
            buf.append(server);
            buf.append('.');
            flag = true;
        }

        if (database != null) {
            buf.append(database);
            buf.append('.');
            flag = true;
        } else {
            if (flag) {
                buf.append('.');
            }
        }

        if (schema != null) {
            buf.append(schema);
            buf.append('.');
            flag = true;
        } else {
            if (flag) {
                buf.append('.');
            }
        }

        buf.append(object);
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

}
