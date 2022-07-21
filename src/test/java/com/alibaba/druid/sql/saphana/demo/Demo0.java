package com.alibaba.druid.sql.saphana.demo;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.saphana.parser.SAPHanaStatementParser;
import com.alibaba.druid.sql.dialect.saphana.visitor.SAPHanaOutputVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import junit.framework.TestCase;

import java.util.List;

/**
 * @author nukiyoam
 */
public class Demo0 extends TestCase {

    public void test_demo_0() {
        String sql = "SELECT * FROM sys.tables WHERE table_name = 'VBAK'";

        // parser得到AST
        SQLStatementParser parser = new SAPHanaStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList(); //

        // 将AST通过visitor输出
        StringBuilder out = new StringBuilder();
        SAPHanaOutputVisitor visitor = new SAPHanaOutputVisitor(out);

        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
            out.append(";");
        }

        System.out.println(out);
    }
}
