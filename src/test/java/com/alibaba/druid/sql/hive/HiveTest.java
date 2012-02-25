package com.alibaba.druid.sql.hive;

import java.util.List;

import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveOutputVisitor;

public class HiveTest extends TestCase {

    protected String output(List<SQLStatement> stmtList) {
        StringBuilder out = new StringBuilder();
        HiveOutputVisitor visitor = new HiveOutputVisitor(out);

        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }

        return out.toString();
    }
}
