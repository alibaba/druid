package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleFileSpecification extends OracleSQLObjectImpl {

    private static final long serialVersionUID = 1L;

    private List<SQLExpr>     fileNames        = new ArrayList<SQLExpr>();

    private SQLExpr           size;

    private boolean           autoExtendOff    = false;

    private SQLExpr           autoExtendOn;

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, fileNames);
            acceptChild(visitor, size);
            acceptChild(visitor, autoExtendOn);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getAutoExtendOn() {
        return autoExtendOn;
    }

    public void setAutoExtendOn(SQLExpr autoExtendOn) {
        this.autoExtendOn = autoExtendOn;
    }

    public SQLExpr getSize() {
        return size;
    }

    public void setSize(SQLExpr size) {
        this.size = size;
    }

    public boolean isAutoExtendOff() {
        return autoExtendOff;
    }

    public void setAutoExtendOff(boolean autoExtendOff) {
        this.autoExtendOff = autoExtendOff;
    }

    public List<SQLExpr> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<SQLExpr> fileNames) {
        this.fileNames = fileNames;
    }

}
