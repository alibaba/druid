package com.alibaba.druid.bvt.sql.oracle.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Fix Oracle PL/SQL TYPE ... IS RECORD(...) parsing in package body and declare blocks.
 * <p>
 * The parser previously threw "TODO" when encountering RECORD after TYPE ... IS,
 * only supporting REF CURSOR, TABLE OF, and VARRAY.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6589">Issue #6589</a>
 */
public class Issue6589 {
    @Test
    public void test_type_is_record_in_package_body_procedure() {
        String sql = "create or replace PACKAGE BODY pkg_test IS\n"
                + "    procedure test_proc as\n"
                + "        type T_Rec is record(\n"
                + "            DeferredDate date,\n"
                + "            LicenseText  varchar2(300));\n"
                + "        type T_Rec_List is table of T_Rec;\n"
                + "        v_items T_Rec_List;\n"
                + "    begin\n"
                + "        null;\n"
                + "    end test_proc;\n"
                + "END pkg_test;";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.oracle);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
        assertNotNull(stmtList.get(0));
    }

    @Test
    public void test_type_is_record_in_declare_block() {
        String sql = "DECLARE\n"
                + "    type T_Rec is record(id number, name varchar2(100));\n"
                + "    v_rec T_Rec;\n"
                + "BEGIN\n"
                + "    v_rec.id := 1;\n"
                + "    v_rec.name := 'test';\n"
                + "END;";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.oracle);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_type_is_record_multiple_fields() {
        String sql = "create or replace PACKAGE BODY pkg_test IS\n"
                + "    procedure test_proc as\n"
                + "        type T_Employee is record(\n"
                + "            emp_id number,\n"
                + "            emp_name varchar2(200),\n"
                + "            hire_date date,\n"
                + "            salary number(10,2));\n"
                + "    begin\n"
                + "        null;\n"
                + "    end test_proc;\n"
                + "END pkg_test;";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.oracle);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_ref_cursor_still_works() {
        String sql = "create or replace PACKAGE pkg_test AS\n"
                + "    TYPE t_cursor IS REF CURSOR;\n"
                + "    procedure get_data(p_result out t_cursor);\n"
                + "END pkg_test;";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.oracle);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_table_of_still_works() {
        String sql = "create or replace PACKAGE BODY pkg_test IS\n"
                + "    TYPE t_list IS TABLE OF VARCHAR2(200);\n"
                + "    v_items t_list;\n"
                + "END pkg_test;";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.oracle);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_package_spec_and_body_with_record() {
        String sql = "create or replace PACKAGE pkg_test AS\n"
                + "    TYPE t_cursor IS REF CURSOR;\n"
                + "    procedure test_proc(p_result out t_cursor);\n"
                + "END pkg_test;\n"
                + "/\n"
                + "create or replace PACKAGE BODY pkg_test IS\n"
                + "    procedure test_proc(p_result out t_cursor) as\n"
                + "        type T_Rec is record(id number, name varchar2(100));\n"
                + "    begin\n"
                + "        null;\n"
                + "    end test_proc;\n"
                + "END pkg_test;\n"
                + "/";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.oracle);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertNotNull(stmtList);
        assertTrue(stmtList.size() >= 2);
    }
}
