package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

/**
 * @author Dagon0577
 * @date 2019/11/7 10:50
 */
public class MySqlSelectTest_247 extends MysqlTest {
    public void test_0() throws Exception {
        String  sql = "SELECT DISTINCT k.CONSTRAINT_NAME, k.COLUMN_NAME, k.REFERENCED_TABLE_NAME, "
                + "k.REFERENCED_COLUMN_NAME /*!50116 , c.update_rule, c.delete_rule */ FROM "
                + "information_schema.key_column_usage k /*!50116 INNER JOIN "
                + "information_schema.referential_constraints c ON c.constraint_name = k.constraint_name AND "
                + "c.table_name = 'activate_log' */ WHERE k.table_name = 'activate_log' AND k.table_schema = "
                + "'b_yaoking_cn' /*!50116 AND c.constraint_schema = 'b_yaoking_cn' */ AND "
                + "k.REFERENCED_COLUMN_NAME is not NULL";


        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils
                .parseSingleStatement(sql, JdbcConstants.MYSQL);

        assertEquals("SELECT DISTINCT k.CONSTRAINT_NAME, k.COLUMN_NAME, k.REFERENCED_TABLE_NAME, k.REFERENCED_COLUMN_NAME /*!50116 , c.update_rule, c.delete_rule */\n"
                + "FROM information_schema.key_column_usage k /*!50116 INNER JOIN information_schema.referential_constraints c ON c.constraint_name = k.constraint_name AND c.table_name = 'activate_log' */\n"
                + "WHERE k.table_name = 'activate_log'\n"
                + "\tAND k.table_schema = 'b_yaoking_cn' /*!50116 AND c.constraint_schema = 'b_yaoking_cn' */\n"
                + "\tAND k.REFERENCED_COLUMN_NAME IS NOT NULL", stmt.toString());

        sql = "SELECT SysNo,MATCH(Name)AGAINST('张三' WITH QUERY EXPANSION) AS score\n"
                + "FROM `test_table`\n" + "WHERE MATCH(Name)AGAINST('张三' WITH QUERY EXPANSION)";

        stmt = (SQLSelectStatement) SQLUtils
                .parseSingleStatement(sql, JdbcConstants.MYSQL);

        assertEquals("SELECT SysNo, MATCH (Name) AGAINST ('张三' WITH QUERY EXPANSION) AS score\n"
                + "FROM `test_table`\n" + "WHERE MATCH (Name) AGAINST ('张三' WITH QUERY EXPANSION)", stmt.toString());
    }
}
