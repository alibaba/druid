package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * version 1.0
 * Author zzy
 * Date 2019-07-22 17:01
 */
public class DrdsChangeDDLJob extends MySqlStatementImpl implements SQLStatement {

    private long jobId = 0;
    private boolean skip = false;
    private boolean add = false;
    private List<String> groupAndTableNameList = new ArrayList<String>();

    public void accept0(MySqlASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public List<String> getGroupAndTableNameList() {
        return groupAndTableNameList;
    }

    public void addGroupAndTableNameList(String groupAndTableName) {
        this.groupAndTableNameList.add(groupAndTableName);
    }

}
