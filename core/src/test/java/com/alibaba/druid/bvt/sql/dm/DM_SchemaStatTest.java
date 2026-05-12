package com.alibaba.druid.bvt.sql.dm;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DM_SchemaStatTest {
    private final DbType dbType = DbType.dm;

    @Test
    public void test_schema_stat_select() {
        String sql = "SELECT a.id, a.name, b.dept_name FROM employees a JOIN departments b ON a.dept_id = b.id WHERE a.status = 1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmtList.get(0).accept(visitor);

        assertEquals(2, visitor.getTables().size());
        assertTrue(visitor.getColumns().size() > 0);
    }

    @Test
    public void test_schema_stat_insert() {
        String sql = "INSERT INTO t1 (id, name) VALUES (1, 'test')";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmtList.get(0).accept(visitor);

        assertEquals(1, visitor.getTables().size());
    }

    @Test
    public void test_schema_stat_merge() {
        String sql = "MERGE INTO t1 a USING t2 b ON (a.id = b.id) " +
                "WHEN MATCHED THEN UPDATE SET a.name = b.name " +
                "WHEN NOT MATCHED THEN INSERT (id, name) VALUES (b.id, b.name)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmtList.get(0).accept(visitor);

        assertEquals(2, visitor.getTables().size());
    }
}
