package com.alibaba.druid.sql.dialect.sqlserver.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerObjectImpl;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;

public class SQLServerObjectReferenceExpr extends SQLServerObjectImpl implements SQLServerExpr, SQLName {

    private static final long serialVersionUID = 1L;

    private String            server;
    private String            database;
    private String            schema;
    private String            object;
    
    public SQLServerObjectReferenceExpr() {
        
    }
    
    public SQLServerObjectReferenceExpr(SQLExpr owner, String name) {
        if (owner instanceof SQLIdentifierExpr) {
            this.database = ((SQLIdentifierExpr) owner).getName();
            this.object = name;
        } else if (owner instanceof SQLPropertyExpr) {
            SQLPropertyExpr propExpr = (SQLPropertyExpr) owner;
            
            this.server = ((SQLIdentifierExpr) propExpr.getOwner()).getName();
            this.database = propExpr.getName();
            this.object = name;
        } else {
            throw new IllegalArgumentException(owner.toString());
        }
    }
    
    @Override
    public void accept0(SQLServerASTVisitor visitor) {
        if (visitor.visit(this)) {
            
        }
        visitor.endVisit(this);
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
