package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Test;

import java.util.List;

/**
 * @author chenmo.cm
 * @date 2018/12/12 10:01 AM
 */
public class MySqlCreateIndexTest_11_adb extends MysqlTest {

    @Test
    public void test_0() throws Exception {
        String sql = "CREATE FULLTEXT INDEX `g_i_seller` ON t_order (`seller_id`)";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("CREATE FULLTEXT INDEX `g_i_seller` ON t_order (`seller_id`)", output);
    }

    @Test
    public void test_1() throws Exception {
        String sql = "CREATE CLUSTERED INDEX `g_i_seller` ON t_order (`seller_id`)";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("CREATE CLUSTERED INDEX `g_i_seller` ON t_order (`seller_id`)", output);
    }

}
