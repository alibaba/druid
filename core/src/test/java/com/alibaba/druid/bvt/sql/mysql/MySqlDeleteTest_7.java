package com.alibaba.druid.bvt.sql.mysql;

import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import static org.junit.Assert.*;
import org.junit.Test;

public class MySqlDeleteTest_7 {
    @Test
    public void test_0() throws Exception {
    String sql =
        "delete t1.* from xxx t1 join yyy t2 \n"
            + "on t1.id = t2 .id \n"
            + "where t1.c1 in ('1','2') and t2.c2 in ('1','2');";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLDeleteStatement stmt = (SQLDeleteStatement) statementList.get(0);
        assertEquals("t1.*", stmt.getTableName().toString());
    }
}
