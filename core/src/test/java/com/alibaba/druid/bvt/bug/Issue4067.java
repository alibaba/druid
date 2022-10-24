package com.alibaba.druid.bvt.bug;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import junit.framework.TestCase;

public class Issue4067 extends TestCase {
    public void test_for_issue() throws Exception {
        List<SQLStatement> stmtList = SQLUtils.parseStatements("desc bi.aaa", DbType.hive);
        SQLStatement stmt = stmtList.get(0);
        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(DbType.hive);
        stmt.accept(statVisitor);
        assertEquals(1, statVisitor.getTables().size());
        TableStat tableStat = statVisitor.getTableStat("bi.aa");
        assertEquals(0, tableStat.getDropIndexCount());
    }
}
