package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;

/**
 * @version 1.0
 * @ClassName MySqlSelectTest_282_hint_in_where
 * @description
 * @Author zzy
 * @Date 2019-05-23 15:28
 */
public class MySqlSelectTest_282_hint_in_where extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select `api_menu_groups`.* from `api_menu_groups` where 1 = 1 /*TDDL:MASTER*/ and `api_menu_groups`.`project_id` = 3 order by `api_menu_groups`.`sort` asc limit 10000\n";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.TDDLHint);

        assertEquals("SELECT /*TDDL:MASTER*/ `api_menu_groups`.*\n" +
                "FROM `api_menu_groups`\n" +
                "WHERE 1 = 1\n" +
                "\tAND `api_menu_groups`.`project_id` = 3\n" +
                "ORDER BY `api_menu_groups`.`sort` ASC\n" +
                "LIMIT 10000", stmt.toString());
    }

    public void test_1() throws Exception {
        String sql = "select `api_menu_groups`.* from `api_menu_groups` where 1 = 1 /!TDDL:MASTER*/ and `api_menu_groups`.`project_id` = 3 order by `api_menu_groups`.`sort` asc limit 10000\n";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.TDDLHint);

        assertEquals("SELECT /*TDDL:MASTER*/ `api_menu_groups`.*\n" +
                "FROM `api_menu_groups`\n" +
                "WHERE 1 = 1\n" +
                "\tAND `api_menu_groups`.`project_id` = 3\n" +
                "ORDER BY `api_menu_groups`.`sort` ASC\n" +
                "LIMIT 10000", stmt.toString());
    }

    public void test_2() throws Exception {
        String sql = "select `api_menu_groups`.* from `api_menu_groups` where 1 = 1 /*+TDDL:MASTER*/ and `api_menu_groups`.`project_id` = 3 order by `api_menu_groups`.`sort` asc limit 10000\n";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.TDDLHint);

        assertEquals("SELECT /*+TDDL:MASTER*/ `api_menu_groups`.*\n" +
                "FROM `api_menu_groups`\n" +
                "WHERE 1 = 1\n" +
                "\tAND `api_menu_groups`.`project_id` = 3\n" +
                "ORDER BY `api_menu_groups`.`sort` ASC\n" +
                "LIMIT 10000", stmt.toString());
    }

    public void test_3() throws Exception {
        String sql = "select `api_menu_groups`.* from `api_menu_groups` where 1 = 1 /!+TDDL:MASTER*/ and `api_menu_groups`.`project_id` = 3 order by `api_menu_groups`.`sort` asc limit 10000\n";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.TDDLHint);

        assertEquals("SELECT /*+TDDL:MASTER*/ `api_menu_groups`.*\n" +
                "FROM `api_menu_groups`\n" +
                "WHERE 1 = 1\n" +
                "\tAND `api_menu_groups`.`project_id` = 3\n" +
                "ORDER BY `api_menu_groups`.`sort` ASC\n" +
                "LIMIT 10000", stmt.toString());
    }
}
