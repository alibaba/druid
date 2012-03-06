package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.StorageItem;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleCreateTableStatement extends SQLCreateTableStatement implements OracleStatement {

    private static final long serialVersionUID  = 1L;

    private SQLName           tablespace;

    private SQLSelect         select;

    private boolean           inMemoryMetadata;

    private boolean           cursorSpecificSegment;

    // NOPARALLEL
    private Boolean           parallel;

    private List<StorageItem> storage           = new ArrayList<StorageItem>();

    private boolean           organizationIndex = false;

    public boolean isOrganizationIndex() {
        return organizationIndex;
    }

    public void setOrganizationIndex(boolean organizationIndex) {
        this.organizationIndex = organizationIndex;
    }

    public Boolean getParallel() {
        return parallel;
    }

    public void setParallel(Boolean parallel) {
        this.parallel = parallel;
    }

    public boolean isCursorSpecificSegment() {
        return cursorSpecificSegment;
    }

    public void setCursorSpecificSegment(boolean cursorSpecificSegment) {
        this.cursorSpecificSegment = cursorSpecificSegment;
    }

    public boolean isInMemoryMetadata() {
        return inMemoryMetadata;
    }

    public void setInMemoryMetadata(boolean inMemoryMetadata) {
        this.inMemoryMetadata = inMemoryMetadata;
    }

    public SQLName getTablespace() {
        return tablespace;
    }

    public void setTablespace(SQLName tablespace) {
        this.tablespace = tablespace;
    }

    public SQLSelect getSelect() {
        return select;
    }

    public void setSelect(SQLSelect select) {
        this.select = select;
    }

    protected void accept0(SQLASTVisitor visitor) {
        accept0((OracleASTVisitor) visitor);
    }

    public List<StorageItem> getStorage() {
        return storage;
    }

    public void setStorage(List<StorageItem> storage) {
        this.storage = storage;
    }

    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, name);
            this.acceptChild(visitor, tableElementList);
            this.acceptChild(visitor, tablespace);
            this.acceptChild(visitor, select);
            this.acceptChild(visitor, storage);
        }
        visitor.endVisit(this);
    }

}
