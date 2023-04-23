package com.alibaba.druid.bvt.sql.oracle.visitor;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * UNION单元测试
 *
 * @author <a href="mailto:winjay_chan@qq.com">Winajy</a>
 */
public class OracleOutputVisitorTest_Union {
    @Test
    public void testParseUnion_01() {
        String sql = "select count(*)" +
                "from (" +
                "    (select id from tb_user where id in(1,2) order by id)" +
                "    union" +
                "    (select id from tb_user where id=4 )" +
                "    union" +
                "    (select id from tb_user where id=5 )" +
                "" +
                ") t";
        String expectedSql = "SELECT count(*)\n" +
                "FROM (\n" +
                "\t(SELECT id\n" +
                "\tFROM tb_user\n" +
                "\tWHERE id IN (1, 2)\n" +
                "\tORDER BY id)\n" +
                "\tUNION\n" +
                "\t(SELECT id\n" +
                "\tFROM tb_user\n" +
                "\tWHERE id = 4)\n" +
                "\tUNION\n" +
                "\t(SELECT id\n" +
                "\tFROM tb_user\n" +
                "\tWHERE id = 5)\n" +
                ") t";
        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        StringBuilder buf = new StringBuilder();
        OracleOutputVisitor outputVisitor = new OracleOutputVisitor(buf);
        stmt.accept(outputVisitor);

        String convertedSql = buf.toString();
        Assert.assertEquals(expectedSql, convertedSql);
    }

    @Test
    public void testParseUnion_02() {
        String sql = "select *\n" +
                "from (\n" +
                "    (select * from tb_user where id in(1,2) order by id )\n" +
                "         union (select id from tb_user where id=5 order by id)\n" +
                "    \n" +
                "    union\n" +
                "    select * from tb_user where id=4 order by id desc\n" +
                "\n" +
                ") t;";

        String expectedSql = "SELECT *\n" +
                "FROM (\n" +
                "\t(SELECT *\n" +
                "\tFROM tb_user\n" +
                "\tWHERE id IN (1, 2)\n" +
                "\tORDER BY id)\n" +
                "\tUNION\n" +
                "\t(SELECT id\n" +
                "\tFROM tb_user\n" +
                "\tWHERE id = 5\n" +
                "\tORDER BY id)\n" +
                "\tUNION\n" +
                "\t(SELECT *\n" +
                "\tFROM tb_user\n" +
                "\tWHERE id = 4\n" +
                "\tORDER BY id DESC)\n" +
                ") t;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        StringBuilder buf = new StringBuilder();
        OracleOutputVisitor outputVisitor = new OracleOutputVisitor(buf);
        stmt.accept(outputVisitor);

        String convertedSql = buf.toString();
        Assert.assertEquals(expectedSql, convertedSql);
    }
}
