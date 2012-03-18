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

    public SQLServerObjectReferenceExpr(){

    }

    public SQLServerObjectReferenceExpr(SQLExpr owner){
        if (owner instanceof SQLIdentifierExpr) {
            this.database = ((SQLIdentifierExpr) owner).getName();
        } else if (owner instanceof SQLPropertyExpr) {
            SQLPropertyExpr propExpr = (SQLPropertyExpr) owner;

            this.server = ((SQLIdentifierExpr) propExpr.getOwner()).getName();
            this.database = propExpr.getName();
        } else {
            throw new IllegalArgumentException(owner.toString());
        }
    }
    
    public String getSimleName() {
        if (schema != null) {
            return schema;
        }
        
        if (database != null) {
            return database;
        }
        return server;
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
            flag = true;
        }

        if (flag) {
            buf.append('.');
        }
        if (database != null) {
            buf.append(database);
            flag = true;
        } 

        if (flag) {
            buf.append('.');
        }
        
        if (schema != null) {
            buf.append(schema);
            flag = true;
        }
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

}
