package com.alibaba.druid.bvt.sql.oracle.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @see <a href="https://github.com/alibaba/druid/issues/6589">Issue 6589</a>
 * Oracle PL/SQL parsing fails on TYPE ... IS TABLE OF inside package body.
 */
public class Issue6589 {
    @Test
    public void test_type_is_table_of_in_package_body() {
        String sql = "CREATE OR REPLACE PACKAGE BODY my_pkg AS\n"
                + "  type T_Validator_List is table of varchar2(200);\n"
                + "  PROCEDURE my_proc IS\n"
                + "  BEGIN\n"
                + "    NULL;\n"
                + "  END;\n"
                + "END;";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.oracle);
        List<SQLStatement> statementList = parser.parseStatementList();
        assertEquals(1, statementList.size());
    }

    @Test
    public void test_type_is_table_of_index_by_in_package_body() {
        String sql = "CREATE OR REPLACE PACKAGE BODY my_pkg AS\n"
                + "  type T_Name_List is table of varchar2(100) index by binary_integer;\n"
                + "  PROCEDURE my_proc IS\n"
                + "  BEGIN\n"
                + "    NULL;\n"
                + "  END;\n"
                + "END;";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.oracle);
        List<SQLStatement> statementList = parser.parseStatementList();
        assertEquals(1, statementList.size());
    }
}
