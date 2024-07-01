package com.alibaba.druid.bvt.sql.postgresql;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;

import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class PGDoSQLTest  extends PGTest {
    public void testDoSQL(){
        String sql1="DO $do$ BEGIN IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 't_pg_do_test') THEN\n"
            + "CREATE TABLE T_PG_DO_TEST (id BIGINT NOT NULL, content CHARACTER VARYING (36) DEFAULT ' ');\n"
            + "END IF; END; $do$;";
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
            + "END;\n"
            + "$do$;";
        System.out.println(sql1);
        System.out.println(sql1Result);
        equal(sql1,sql1Result);
    }


    private void equal(String targetSql,String resultSql){
        PGSQLStatementParser parser=new PGSQLStatementParser(targetSql);
        SQLStatement statement = parser.parseStatement();
        assertEquals(resultSql,statement.toString());

    }

    public void testDoSQL_WithDeclare_NoTagAndLabel() {
        String sql = "DO $$\n" +
                "DECLARE\n" +
                "\tn INTEGER;\n" +
                "BEGIN\n" +
                "\tGET DIAGNOSTICS n = ROW_COUNT;\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statementList.size());

        String output = SQLUtils.toSQLString(statementList, JdbcConstants.POSTGRESQL);
        assertEquals(sql, output);
    }

    public void testDoSQL_WithDeclare_TagWithoutLabel() {
        String sql = "DO $tag1$\n" +
                "DECLARE\n" +
                "\tn INTEGER;\n" +
                "BEGIN\n" +
                "\tGET DIAGNOSTICS n = ROW_COUNT;\n" +
                "END;\n" +
                "$tag1$ LANGUAGE plpgsql;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statementList.size());

        String output = SQLUtils.toSQLString(statementList, JdbcConstants.POSTGRESQL);
        assertEquals(sql, output);
    }
    
    // TODO: Need to improve parser to support the label in format "<<label1>>"
    public void testDoSQL_WithDeclare_LabelWithoutTag_NoEndLabel() {
        String sql = "DO $$\n" +
                "label1\n" +
                "DECLARE\n" +
                "\tn INTEGER;\n" +
                "BEGIN\n" +
                "\tGET DIAGNOSTICS n = ROW_COUNT;\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statementList.size());

        String output = SQLUtils.toSQLString(statementList, JdbcConstants.POSTGRESQL);
        assertEquals(sql, output);
    }

    // TODO: Need to improve parser to support the label in format "<<label1>>"
    public void testDoSQL_WithDeclare_LabelWithoutTag_WithEndLabel() {
        String sql = "DO $$\n" +
                "label1\n" +
                "DECLARE\n" +
                "\tn INTEGER;\n" +
                "BEGIN\n" +
                "\tGET DIAGNOSTICS n = ROW_COUNT;\n" +
                "END label1;\n" +
                "$$ LANGUAGE plpgsql;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statementList.size());

        String output = SQLUtils.toSQLString(statementList, JdbcConstants.POSTGRESQL);
        assertEquals(sql, output);
    }

    // TODO: Need to improve parser to support the label in format "<<label1>>"
    public void testDoSQL_WithDeclare_TagAndLabel_NoEndLabel() {
        String sql = "DO $tag1$\n" +
                "label1\n" +
                "DECLARE\n" +
                "\tn INTEGER;\n" +
                "BEGIN\n" +
                "\tGET DIAGNOSTICS n = ROW_COUNT;\n" +
                "END;\n" +
                "$tag1$ LANGUAGE plpgsql;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statementList.size());

        String output = SQLUtils.toSQLString(statementList, JdbcConstants.POSTGRESQL);
        assertEquals(sql, output);
    }

    // TODO: Need to improve parser to support the label in format "<<label1>>"
    public void testDoSQL_WithDeclare_TagAndLabel_WithEndLabel() {
        String sql = "DO $tag1$\n" +
                "label1\n" +
                "DECLARE\n" +
                "\tn INTEGER;\n" +
                "BEGIN\n" +
                "\tGET DIAGNOSTICS n = ROW_COUNT;\n" +
                "END label1;\n" +
                "$tag1$ LANGUAGE plpgsql;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statementList.size());

        String output = SQLUtils.toSQLString(statementList, JdbcConstants.POSTGRESQL);
        assertEquals(sql, output);
    }
}
