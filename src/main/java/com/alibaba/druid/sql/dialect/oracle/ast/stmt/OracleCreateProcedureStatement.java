package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleParameter;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleCreateProcedureStatement extends OracleStatementImpl {

    private static final long     serialVersionUID = 1L;

    private boolean               orReplace;
    private SQLName               name;
    private OracleBlockStatement  block;
    private List<OracleParameter> parameters       = new ArrayList<OracleParameter>();

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, parameters);
            acceptChild(visitor, block);
        }
        visitor.endVisit(this);
    }

    public List<OracleParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<OracleParameter> parameters) {
        this.parameters = parameters;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public OracleBlockStatement getBlock() {
        return block;
    }

    public void setBlock(OracleBlockStatement block) {
        this.block = block;
    }

    public boolean isOrReplace() {
        return orReplace;
    }

    public void setOrReplace(boolean orReplace) {
        this.orReplace = orReplace;
    }

}
