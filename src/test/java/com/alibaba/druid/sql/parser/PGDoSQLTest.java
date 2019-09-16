package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import junit.framework.TestCase;
import org.junit.Assert;

/**
 * Created by tianzhen.wtz on 2014/12/26 0026 20:44.
 * 类说明：
 */
public class PGDoSQLTest extends TestCase{


    public void testDoSQL(){
        String sql1="DO $do$ BEGIN IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 't_pg_do_test') THEN\n"
            + "CREATE TABLE T_PG_DO_TEST (id BIGINT NOT NULL, content CHARACTER VARYING (36) DEFAULT ' ');\n"
            + "END IF; END $do$;";
        String sql1Result="DO $do$\n"
            + "BEGIN\n"
            + "\tIF NOT EXISTS (\n"
            + "\t\t\tSELECT 1\n"
            + "\t\t\tFROM information_schema.tables\n"
            + "\t\t\tWHERE table_name = 't_pg_do_test'\n"
            + "\t\t)\n"
            + "\tTHEN\n"
            + "\t\tCREATE TABLE T_PG_DO_TEST (\n"
            + "\t\t\tid BIGINT NOT NULL,\n"
            + "\t\t\tcontent CHARACTER VARYING(36) DEFAULT ' '\n"
            + "\t\t);\n"
            + "\tEND IF;\n"
            + "END $do$";
        equal(sql1,sql1Result);
    }


    private void equal(String targetSql,String resultSql){
        PGSQLStatementParser parser=new PGSQLStatementParser(targetSql);
        SQLStatement statement = parser.parseStatement();
        Assert.assertTrue(statement.toString().equals(resultSql));

    }

}
