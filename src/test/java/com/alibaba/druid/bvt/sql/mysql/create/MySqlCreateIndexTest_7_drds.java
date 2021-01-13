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
public class MySqlCreateIndexTest_7_drds extends MysqlTest {

    @Test
    public void test_one() throws Exception {
        String sql = "CREATE GLOBAL INDEX `g_i_seller` ON t_order (`seller_id`) COVERING (order_snapshot) "
            + "dbpartition by hash(`seller_id`) tbpartition by hash(`seller_id`) tbpartitions 3;";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("CREATE GLOBAL INDEX `g_i_seller` ON t_order (`seller_id`) COVERING (order_snapshot) "
                + "DBPARTITION BY hash(`seller_id`) TBPARTITION BY hash(`seller_id`) TBPARTITIONS 3;",
            output);
    }

    @Test
    public void test_two() throws Exception {
        String sql = "CREATE GLOBAL INDEX `g_i_seller` ON t_order (`seller_id`) COVERING (order_snapshot) "
            + "dbpartition by hash(`seller_id`) tbpartition by hash(`seller_id`) tbpartitions 3 "
            + "COMMENT 'CREATE GSI TEST';";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("CREATE GLOBAL INDEX `g_i_seller` ON t_order (`seller_id`) COVERING (order_snapshot) "
                + "DBPARTITION BY hash(`seller_id`) TBPARTITION BY hash(`seller_id`) TBPARTITIONS 3 "
                + "COMMENT 'CREATE GSI TEST';",
            output);
    }

    @Test
    public void test_three() throws Exception {
        String sql = "CREATE GLOBAL INDEX `g_i_seller` ON t_order (`seller_id`) COVERING (order_snapshot) "
            + "dbpartition by hash(`seller_id`) tbpartition by hash(`seller_id`) tbpartitions 3 "
            + "USING BTREE KEY_BLOCK_SIZE=20 COMMENT 'CREATE GSI TEST' ALGORITHM=DEFAULT LOCK=DEFAULT;";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("CREATE GLOBAL INDEX `g_i_seller` ON t_order (`seller_id`) COVERING (order_snapshot) DBPARTITION BY hash(`seller_id`) TBPARTITION BY hash(`seller_id`) TBPARTITIONS 3 USING BTREE KEY_BLOCK_SIZE = 20 COMMENT 'CREATE GSI TEST' ALGORITHM = DEFAULT LOCK = DEFAULT;",
            output);
    }
}
