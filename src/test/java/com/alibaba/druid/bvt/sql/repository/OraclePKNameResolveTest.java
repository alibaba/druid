package com.alibaba.druid.bvt.sql.repository;

import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

/**
 * Created by wenshao on 03/08/2017.
 */
public class OraclePKNameResolveTest extends TestCase {
    protected SchemaRepository repository = new SchemaRepository(JdbcConstants.ORACLE);

    public void test_for_issue() throws Exception {
        String sql_1 = "create table a(\n" +
                " WORKITEMID VARCHAR(40) NOT NULL,\n" +
                "CONSTRAINT PRIMARY_WORKTASK PRIMARY KEY (WORKITEMID)\n" +
                ")";
        String sql_2 = "create table b(\n" +
                " WORKITEMID VARCHAR(40) NOT NULL,\n" +
                "CONSTRAINT PRIMARY_WORKTASK PRIMARY KEY (WORKITEMID)\n" +
                ")"
                ;

        repository.resolve(sql_1);

        OracleCreateTableStatement stmt = (OracleCreateTableStatement) SQLParserUtils.createSQLStatementParser(sql_2, JdbcConstants.ORACLE).parseStatement();

        repository.resolve(stmt);
        System.out.println(stmt);
    }
}
