package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleBlockTest6 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "declare" + //
                     "     l_line varchar2(32767);" + //
                     "     l_done number;" + //
                     "     l_buffer varchar2(32767) := '';" + //
                     "    l_lengthbuffer number := 0;" + //
                     "    l_lengthline number := 0;  " + //
                     "begin" + //
                     "   loop" + //
                     "     dbms_output.get_line( l_line, l_done );" + //
                     "     if (l_buffer is null) then" + //
                     "       l_lengthbuffer := 0;" + //
                     "     else" + //
                     "       l_lengthbuffer := length(l_buffer);" + //
                     "     end if;" + //
                     "     if (l_line is null) then" + //
                     "       l_lengthline := 0;" + //
                     "     else" + //
                     "       l_lengthline := length(l_line);" + //
                     "     end if;" + //
                     "     exit when l_lengthbuffer + l_lengthline > :maxbytes " + //
                     "          OR l_lengthbuffer + l_lengthline > 32767 OR l_done = 1;" + //
                     "     l_buffer := l_buffer || l_line || chr(10);" + //
                     "   end loop;" + //
                     "  :done := l_done;" + //
                     "  :buffer := l_buffer;" + //
                     "  :line := l_line; " + //
                     "end;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(0, visitor.getTables().size());

//        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("escrow_trade")));

        Assert.assertEquals(0, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("departments", "department_id")));
    }
}
