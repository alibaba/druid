package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.apache.ibatis.jdbc.SQL;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenshao on 23/05/2017.
 */
public class OracleCreatePackageStatement extends OracleStatementImpl {
    private boolean            orReplace;
    private SQLName name;

    private boolean body;

    private final List<SQLStatement> statements = new ArrayList<SQLStatement>();

    public OracleCreatePackageStatement() {
        super.setDbType(JdbcConstants.ORACLE);
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, statements);
        }
        visitor.endVisit(this);
    }

    public boolean isOrReplace() {
        return orReplace;
    }

    public void setOrReplace(boolean orReplace) {
        this.orReplace = orReplace;
    }

    public boolean isBody() {
        return body;
    }

    public void setBody(boolean body) {
        this.body = body;
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

    public List<SQLStatement> getStatements() {
        return statements;
    }
}
