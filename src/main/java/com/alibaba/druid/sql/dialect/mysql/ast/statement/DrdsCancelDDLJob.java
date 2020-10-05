package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * version 1.0
 * Author zzy
 * Date 2019-06-16 21:45
 */
public class DrdsCancelDDLJob extends MySqlStatementImpl implements SQLStatement {

    private List<Long> jobIds = new ArrayList<Long>();

    public void accept0(MySqlASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public List<Long> getJobIds() {
        return jobIds;
    }

    public void addJobId(long id) {
        jobIds.add(id);
    }
}
