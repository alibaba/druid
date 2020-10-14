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
 * @date 2018/12/12 2:23 PM
 */
public class MySqlCreateIndexTest_9_drds extends MysqlTest {

    @Test
    public void test_one() throws Exception {
        String sql = "CREATE GLOBAL INDEX `g_i_seller` ON t_order (`create_time`) COVERING (order_snapshot) "
                     + "dbpartition BY YYYYMM_NOLOOP (create_time) tbpartition BY YYYYMM_NOLOOP (create_time) "
                     + "STARTWITH 20160108 ENDWITH 20170108;";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("CREATE GLOBAL INDEX `g_i_seller` ON t_order (`create_time`) COVERING (order_snapshot) DBPARTITION BY YYYYMM_NOLOOP(create_time) TBPARTITION BY YYYYMM_NOLOOP(create_time) BETWEEN 20160108 AND 20170108;", output);
    }

    @Test
    public void test_two() throws Exception {
        String sql = "CREATE GLOBAL INDEX `g_i_seller` ON t_order (`create_time`) COVERING (order_snapshot) "
                     + "dbpartition BY YYYYMM_NOLOOP (create_time) tbpartition BY YYYYMM_NOLOOP (create_time) "
                     + "STARTWITH 20160108 ENDWITH 20170108 " + "COMMENT 'CREATE GSI TEST';";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("CREATE GLOBAL INDEX `g_i_seller` ON t_order (`create_time`) COVERING (order_snapshot) DBPARTITION BY YYYYMM_NOLOOP(create_time) TBPARTITION BY YYYYMM_NOLOOP(create_time) BETWEEN 20160108 AND 20170108 COMMENT 'CREATE GSI TEST';", output);
    }

    @Test
    public void test_three() throws Exception {
        String sql = "CREATE GLOBAL INDEX `g_i_seller` ON t_order (`create_time`) COVERING (order_snapshot) "
                     + "dbpartition BY YYYYMM_NOLOOP (create_time) tbpartition BY YYYYMM_NOLOOP (create_time) "
                     + "STARTWITH 20160108 ENDWITH 20170108 "
                     + "USING BTREE KEY_BLOCK_SIZE=20 COMMENT 'CREATE GSI TEST' ALGORITHM=DEFAULT LOCK=DEFAULT;";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("CREATE GLOBAL INDEX `g_i_seller` ON t_order (`create_time`) COVERING (order_snapshot) DBPARTITION BY YYYYMM_NOLOOP(create_time) TBPARTITION BY YYYYMM_NOLOOP(create_time) BETWEEN 20160108 AND 20170108 USING BTREE KEY_BLOCK_SIZE = 20 COMMENT 'CREATE GSI TEST' ALGORITHM = DEFAULT LOCK = DEFAULT;",
            output);
    }
}
