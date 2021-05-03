package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLType;
import junit.framework.TestCase;

public class ScanSQLTypeV2Test extends TestCase {
    public void test_sqlTypeV2() throws Exception {
        String sql = "create TABLE IF NOT EXISTS tmp_ventes_20210218_1430 LIFECYCLE 1 AS \n" +
                "select  did, ";

        Lexer lexer = SQLParserUtils.createLexer(sql, DbType.odps);
        SQLType sqlType = lexer.scanSQLTypeV2();
        assertEquals(SQLType.CREATE_TABLE_AS_SELECT, sqlType);
    }

    public void test_sqlTypeV2_1() throws Exception {
        String sql = "CREATE FUNCTION COMBINE_PB_ONLINE as 'UDTFCombinePbV2.CombinePb' USING 'UDTFCombinePbV2.py, fg.json'";

        Lexer lexer = SQLParserUtils.createLexer(sql, DbType.odps);
        SQLType sqlType = lexer.scanSQLTypeV2();
        assertEquals(SQLType.CREATE_FUNCTION, sqlType);
    }

    public void test_sqlTypeV2_2() throws Exception {
        String sql = "WITH\n" +
                "    hub  AS\n" +
                "(\n" +
                "    SELECT  a.id, a.name  FROM  eserve_js.tollinterval a\n" +
                "    LEFT JOIN eserve_js_dev.map_ramp b ON a.id = b.id\n" +
                "    LEFT JOIN eserve_js_dev.map_road c ON a.id = c.id\n" +
                "    WHERE b.id IS NULL AND c.id IS NULL\n" +
                ")\n" +
                "SELECT * FROM hub";

        Lexer lexer = SQLParserUtils.createLexer(sql, DbType.odps);
        SQLType sqlType = lexer.scanSQLTypeV2();
        assertEquals(SQLType.SELECT, sqlType);
    }

    public void test_sqlTypeV2_3() throws Exception {
        String sql = "DROP FUNCTION COMBINE_PB_ONLINE";

        Lexer lexer = SQLParserUtils.createLexer(sql, DbType.odps);
        SQLType sqlType = lexer.scanSQLTypeV2();
        assertEquals(SQLType.DROP_FUNCTION, sqlType);
    }

    public void test_sqlTypeV2_4() throws Exception {
        String sql = "INSERT INTO employee VALUES \n" +
                "(13,'Mari',51,'M'),\n" +
                "(14,'Pat',34,'F');";

        Lexer lexer = SQLParserUtils.createLexer(sql, DbType.odps);
        SQLType sqlType = lexer.scanSQLTypeV2();
        assertEquals(SQLType.INSERT_VALUES, sqlType);
    }

    public void test_sqlTypeV2_5() throws Exception {
        String sql = "\n" +
                "add table xxxx partition(ds='20210306') as 'search_tablebert_output_v_vocab' -f";

        Lexer lexer = SQLParserUtils.createLexer(sql, DbType.odps);
        SQLType sqlType = lexer.scanSQLTypeV2();
        assertEquals(SQLType.ADD_TABLE, sqlType);
    }

    public void test_sqlTypeV2_6() throws Exception {
        String sql = "\n" +
                "tunnel download xx_dev.xxx tem_app_list_dy.txt";

        Lexer lexer = SQLParserUtils.createLexer(sql, DbType.odps);
        SQLType sqlType = lexer.scanSQLTypeV2();
        assertEquals(SQLType.TUNNEL_DOWNLOAD, sqlType);
    }

    public void test_sqlTypeV2_7() throws Exception {
        String sql = "\n" +
                "upload xx \n" +
                "FROM http://xxx/res?id=163890713";

        Lexer lexer = SQLParserUtils.createLexer(sql, DbType.odps);
        SQLType sqlType = lexer.scanSQLTypeV2();
        assertEquals(SQLType.UPLOAD, sqlType);
    }

}
