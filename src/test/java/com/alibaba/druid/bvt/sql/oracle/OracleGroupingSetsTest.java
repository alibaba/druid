package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

public class OracleGroupingSetsTest extends OracleTest {

    public void test_grouping_sets() throws Exception {
        String sql = //
        "select department_id,job_id,avg(salary) from hr.employees group by grouping sets ((department_id,job_id));";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);
        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("SELECT department_id, job_id, AVG(salary)"
                + "\nFROM hr.employees"
                + "\nGROUP BY GROUPING SETS ((department_id, job_id));", SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(1, visitor.getTables().size());
    }
}
