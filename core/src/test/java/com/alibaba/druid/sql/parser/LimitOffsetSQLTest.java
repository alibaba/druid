package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.dialect.oscar.visitor.OscarStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import junit.framework.TestCase;

/**
 * Created by tianzhen.wtz on 2014/12/26 0026 20:44.
 * 类说明：
 */
public class LimitOffsetSQLTest extends TestCase {


    public void testPGLimitOffsetSQL() {
        String sql1 = "SELECT * FROM table1 LIMIT 10 OFFSET 20";
        String sql1Result = new PGSQLStatementParser(sql1).parseSelect().toString()
                .replace("\n", " ");
        ;

        assertEquals(sql1, sql1Result);

        String sql2 = "SELECT * FROM table1 OFFSET 20 LIMIT 10 ";
        String sql2Result = new PGSQLStatementParser(sql2).parseSelect().toString()
                .replace("\n", " ");

        // OFFSET 20 LIMIT 20 会被解析成 OFFSET 20 LIMIT 10
        assertEquals(sql1, sql2Result);

    }

    public void testOscarLimitOffsetSQL() {
        String sql1 = "SELECT * FROM table1 LIMIT 10 OFFSET 20";
        String sql1Result = new OscarStatementParser(sql1).parseSelect().toString()
                .replace("\n", " ");
        ;

        assertEquals(sql1, sql1Result);

        String sql2 = "SELECT * FROM table1 OFFSET 20 LIMIT 10 ";
        String sql2Result = new OscarStatementParser(sql2).parseSelect().toString()
                .replace("\n", " ");

        // OFFSET 20 LIMIT 20 会被解析成 OFFSET 20 LIMIT 10
        assertEquals(sql1, sql2Result);

    }


}
