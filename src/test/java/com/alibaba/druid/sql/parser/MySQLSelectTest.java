package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

/**
 * @author Dagon0577
 * @date 2019/11/5 15:43
 */
public class MySQLSelectTest extends TestCase {
    public void test_demo_0() {
        String  sql = "SELECT DISTINCT k.CONSTRAINT_NAME, k.COLUMN_NAME, k.REFERENCED_TABLE_NAME, "
                + "k.REFERENCED_COLUMN_NAME /*!50116 , c.update_rule, c.delete_rule */ FROM "
                + "information_schema.key_column_usage k /*!50116 INNER JOIN "
                + "information_schema.referential_constraints c ON c.constraint_name = k.constraint_name AND "
                + "c.table_name = 'activate_log' */ WHERE k.table_name = 'activate_log' AND k.table_schema = "
                + "'b_yaoking_cn' /*!50116 AND c.constraint_schema = 'b_yaoking_cn' */ AND "
                + "k.REFERENCED_COLUMN_NAME is not NULL";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        StringBuilder out = new StringBuilder();
        MySqlOutputVisitor visitor = new MySqlOutputVisitor(out);

        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
            out.append(";");
        }

        Assert.assertEquals(out.toString(),"SELECT DISTINCT k.CONSTRAINT_NAME, k.COLUMN_NAME, k.REFERENCED_TABLE_NAME, k.REFERENCED_COLUMN_NAME /*!50116 , c.update_rule, c.delete_rule */\n"
                + "FROM information_schema.key_column_usage k /*!50116 INNER JOIN information_schema.referential_constraints c ON c.constraint_name = k.constraint_name AND c.table_name = 'activate_log' */\n"
                + "WHERE k.table_name = 'activate_log'\n"
                + "\tAND k.table_schema = 'b_yaoking_cn' /*!50116 AND c.constraint_schema = 'b_yaoking_cn' */\n"
                + "\tAND k.REFERENCED_COLUMN_NAME IS NOT NULL;");

        sql = "SELECT SysNo,MATCH(Name)AGAINST('张三' WITH QUERY EXPANSION) AS score\n"
                + "FROM `test_table`\n" + "WHERE MATCH(Name)AGAINST('张三' WITH QUERY EXPANSION)";

        parser = new MySqlStatementParser(sql);
        stmtList = parser.parseStatementList();

        out = new StringBuilder();
        visitor = new MySqlOutputVisitor(out);

        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
            out.append(";");
        }

        Assert.assertEquals(out.toString(),"SELECT SysNo, MATCH (Name) AGAINST ('张三' WITH QUERY EXPANSION) AS score\n"
                + "FROM `test_table`\n" + "WHERE MATCH (Name) AGAINST ('张三' WITH QUERY EXPANSION);");
    }
}
