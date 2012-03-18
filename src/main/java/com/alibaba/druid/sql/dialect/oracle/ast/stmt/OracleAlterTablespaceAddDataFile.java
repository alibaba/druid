package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleAlterTablespaceAddDataFile extends OracleSQLObjectImpl implements OracleAlterTablespaceItem {

    private static final long             serialVersionUID = 1L;

    private List<OracleFileSpecification> files            = new ArrayList<OracleFileSpecification>();

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, files);
        }
        visitor.endVisit(this);
    }

    public List<OracleFileSpecification> getFiles() {
        return files;
    }

    public void setFiles(List<OracleFileSpecification> files) {
        this.files = files;
    }

}
