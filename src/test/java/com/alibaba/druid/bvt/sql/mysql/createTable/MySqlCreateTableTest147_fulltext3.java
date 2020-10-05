package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import junit.framework.TestCase;

import java.util.List;

public class MySqlCreateTableTest147_fulltext3 extends TestCase {

    public void test_0() throws Exception {

        String sql = "CREATE TABLE tbl_custom_analyzer2 (\n" +
                "  `id` int COMMENT '',   \n" +
                "  `title` varchar COMMENT '', \n" +
                "  FULLTEXT INDEX title_fulltext_idx (title) WITH INDEX ANALYZER index_analyzer2 WITH QUERY ANALYZER query_analyzer2 WITH DICT user_dict, \n" +
                "  PRIMARY KEY (`id`) )\n" +
                "  DISTRIBUTED BY HASH(`id`);";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("CREATE TABLE tbl_custom_analyzer2 (\n" +
                "\t`id` int COMMENT '',\n" +
                "\t`title` varchar COMMENT '',\n" +
                "\tFULLTEXT INDEX title_fulltext_idx(title) WITH INDEX ANALYZER index_analyzer2 WITH QUERY ANALYZER query_analyzer2 WITH DICT user_dict,\n" +
                "\tPRIMARY KEY (`id`)\n" +
                ")\n" +
                "DISTRIBUTE BY HASH(`id`);", stmt.toString());
    }

    public void test_1() throws Exception {

        String sql = "CREATE TABLE tbl_custom_analyzer2 (\n" +
                "  `id` int COMMENT '',   \n" +
                "  `title` varchar COMMENT '', \n" +
                "  FULLTEXT INDEX title_fulltext_idx (title) WITH INDEX ANALYZER index_analyzer2 WITH DICT user_dict, \n" +
                "  PRIMARY KEY (`id`) )\n" +
                "  DISTRIBUTED BY HASH(`id`);";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) statementList.get(0);

        assertEquals(1, statementList.size());

        System.out.println(stmt.toString());
        assertEquals("CREATE TABLE tbl_custom_analyzer2 (\n" +
                "\t`id` int COMMENT '',\n" +
                "\t`title` varchar COMMENT '',\n" +
                "\tFULLTEXT INDEX title_fulltext_idx(title) WITH INDEX ANALYZER index_analyzer2 WITH DICT user_dict,\n" +
                "\tPRIMARY KEY (`id`)\n" +
                ")\n" +
                "DISTRIBUTE BY HASH(`id`);", stmt.toString());
    }

    public void test_2() throws Exception {

        String sql = "CREATE TABLE tbl_custom_analyzer2 (\n" +
                "  `id` int COMMENT '',   \n" +
                "  `title` varchar COMMENT '', \n" +
                "  FULLTEXT INDEX title_fulltext_idx (title) WITH QUERY ANALYZER query_analyzer2 WITH DICT user_dict, \n" +
                "  PRIMARY KEY (`id`) )\n" +
                "  DISTRIBUTED BY HASH(`id`);";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) statementList.get(0);

        assertEquals(1, statementList.size());

        System.out.println(stmt.toString());
        assertEquals("CREATE TABLE tbl_custom_analyzer2 (\n" +
                "\t`id` int COMMENT '',\n" +
                "\t`title` varchar COMMENT '',\n" +
                "\tFULLTEXT INDEX title_fulltext_idx(title) WITH QUERY ANALYZER query_analyzer2 WITH DICT user_dict,\n" +
                "\tPRIMARY KEY (`id`)\n" +
                ")\n" +
                "DISTRIBUTE BY HASH(`id`);", stmt.toString());
    }

    public void test_3() throws Exception {

        String sql = "CREATE TABLE tbl_custom_analyzer2 (\n" +
                "  `id` int COMMENT '',   \n" +
                "  `title` varchar COMMENT '', \n" +
                "  FULLTEXT INDEX title_fulltext_idx (title) WITH INDEX ANALYZER index_analyzer2 WITH QUERY ANALYZER query_analyzer2 WITH DICT user_dict, \n" +
                "  PRIMARY KEY (`id`) )\n" +
                "  DISTRIBUTED BY HASH(`id`);";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) statementList.get(0);

        assertEquals(1, statementList.size());

        System.out.println(stmt.toString());
        assertEquals("CREATE TABLE tbl_custom_analyzer2 (\n" +
                "\t`id` int COMMENT '',\n" +
                "\t`title` varchar COMMENT '',\n" +
                "\tFULLTEXT INDEX title_fulltext_idx(title) WITH INDEX ANALYZER index_analyzer2 WITH QUERY ANALYZER query_analyzer2 WITH DICT user_dict,\n" +
                "\tPRIMARY KEY (`id`)\n" +
                ")\n" +
                "DISTRIBUTE BY HASH(`id`);", stmt.toString());
    }

}
