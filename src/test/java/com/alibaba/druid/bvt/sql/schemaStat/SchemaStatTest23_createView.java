package com.alibaba.druid.bvt.sql.schemaStat;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.support.opds.udf.ExportInputTables;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class SchemaStatTest23_createView extends TestCase {

    public void test_schemaStat() throws Throwable {
        SchemaRepository repository = new SchemaRepository(JdbcConstants.ODPS);

        String sql = "DROP VIEW IF EXISTS v1;\n" +
                "CREATE VIEW v1 \n" +
                "AS \n" +
                "SELECT * FROM s1.t1;\n" +
                "select * from v1;";
//        sql = "SELECT * FROM ( SELECT * FROM DEPT) X WHERE X.dname='cs'";

        String inputs = new ExportInputTables()
                .evaluate(sql, "odps");
        assertEquals("s1.t1", inputs);
    }
}
