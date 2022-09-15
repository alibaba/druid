package com.alibaba.druid.bvt.sql.repository;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by wenshao on 03/08/2017.
 */
public class OracleJoinResolveTest_2_join extends TestCase {
    protected SchemaRepository repository = new SchemaRepository(JdbcConstants.ORACLE);

    protected void setUp() throws Exception {
        repository.console("create table t_user (uid number(20, 0), gid number(20, 0), name varchar2(20))");
        repository.console("create table t_group (id number(20, 0), name varchar2(20))");
    }

    public void test_for_issue() throws Exception {
        assertEquals("SELECT a.uid, a.gid, a.name, b.id, b.name\n" +
                        "FROM t_user a\n" +
                        "\tINNER JOIN t_group b\n" +
                        "WHERE a.uid = b.id"
                , repository.resolve("select * from t_user a inner join t_group b where a.uid = id"));

        String sql = "select a.* from t_user a inner join t_group b where a.uid = id";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        SchemaStatVisitor schemaStatVisitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.ORACLE);
        schemaStatVisitor.setRepository(repository);

        for (SQLStatement stmt : statementList) {
            stmt.accept(schemaStatVisitor);
        }

        assertTrue(schemaStatVisitor.containsColumn("t_user", "*"));
        assertTrue(schemaStatVisitor.containsColumn("t_user", "uid"));
        assertTrue(schemaStatVisitor.containsColumn("t_group", "id"));
    }
}
