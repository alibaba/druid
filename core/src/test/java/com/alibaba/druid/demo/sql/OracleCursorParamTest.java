package com.alibaba.druid.demo.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import junit.framework.TestCase;

/**
 * @author LENOVO
 * @date 2024/7/17 10:56
 */
public class OracleCursorParamTest extends TestCase {

    public void test_cursor_parameters() {
        String sql = "DECLARE\n" +
                "CURSOR cur_param(name_str VARCHAR2) IS SELECT u.USERNAME, u.PASSWORD FROM MG_USER u WHERE ID = 1;\n" +
                "op_name VARCHAR2(100);\n" +
                "BEGIN\n" +
                "SELECT NVL(EMP_NAME, USERNAME) INTO op_name FROM MG_USER WHERE ID = 1;\n" +
                "END;";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.oracle);

        System.out.println(stmt);
    }
}
