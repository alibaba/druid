package com.alibaba.druid.bvt.sql.impala;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.impala.stmt.ImpalaCreateTableStatement;
import com.alibaba.druid.sql.dialect.impala.visitor.ImpalaSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class ImpalaCreateTableTest_1 extends TestCase {
    public void test_create() throws Exception {
        String sql = "CREATE EXTERNAL TABLE external_parquet (c1 INT, c2 STRING, c3 TIMESTAMP) STORED AS PARQUET LOCATION '/user/etl/destination';";
        assertEquals("CREATE EXTERNAL TABLE external_parquet (\n" +
            "\tc1 INT,\n" +
            "\tc2 STRING,\n" +
            "\tc3 TIMESTAMP\n" +
            ")\n" +
            "STORE AS PARQUET\n" +
            "LOCATION '/user/etl/destination';", SQLUtils.formatImpala(sql));
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.IMPALA);
        ImpalaCreateTableStatement stmt = (ImpalaCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        ImpalaSchemaStatVisitor visitor =
            (ImpalaSchemaStatVisitor)SQLUtils.createSchemaStatVisitor(JdbcConstants.IMPALA);

        stmt.accept(visitor);
        assertEquals(1, visitor.getTables().size());

        assertEquals(3, visitor.getColumns().size());


    }
}
