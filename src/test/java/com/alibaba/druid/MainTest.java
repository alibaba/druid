package com.alibaba.druid;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcUtils;
import java.util.List;

public class MainTest {

  public static void main(String[] args){
    // pg
    String sql = "DO $do$ BEGIN IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 't_gl_myoperation') THEN\n"
        + "CREATE TABLE T_GL_MYOPERATION (fid BIGINT NOT NULL, fcreatorid BIGINT NOT NULL DEFAULT 0, forgid BIGINT NOT NULL DEFAULT 0, fmenuid CHARACTER VARYING (36) DEFAULT ' ', fcreatetime TIMESTAMP WITHOUT TIME ZONE, fbizappid CHARACTER VARYING (36));\n"
        + "END IF; END $do$;";
//    String sql = "CREATE OR REPLACE FUNCTION test_123()  \n"
//        + "RETURNS void AS  \n"
//        + "$BODY$  \n"
//        + "DECLARE  \n"
//        + "BEGIN  \n"
//        + "\tIF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 't_gl_myoperation') THEN \n"
//        + "\t\tCREATE TABLE T_GL_MYOPERATION (\n"
//        + "\t\t\t\tfid BIGINT NOT NULL, \n"
//        + "\t\t\t\tfcreatorid BIGINT NOT NULL DEFAULT 0, \n"
//        + "\t\t\t\tforgid BIGINT NOT NULL DEFAULT 0, \n"
//        + "\t\t\t\tfmenuid CHARACTER VARYING (36) DEFAULT ' ', \n"
//        + "\t\t\t\tfcreatetime TIMESTAMP WITHOUT TIME ZONE, \n"
//        + "\t\t\t\tfbizappid CHARACTER VARYING (36)\n"
//        + "\t\t); \n"
//        + "\tEND IF; \n"
//        + "END;  \n"
//        + "$BODY$  \n"
//        + "LANGUAGE 'plpgsql';\n"
//        + "\n"
//        + "select test_123();\n"
//        + "\n"
//        + "drop function test_123;";
      List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, JdbcUtils.POSTGRESQL);
      String druidsql = SQLUtils.toSQLString(sqlStatements, JdbcUtils.POSTGRESQL);
      System.out.println(druidsql);


// mysql
//    String sql = "CREATE PROCEDURE `hahayjb`.`KSQL_TEMP_PROCEDURE_1115493502395` ()\n"
//        + "BEGIN\n"
//        + "\tIF NOT EXISTS (\n"
//        + "\t\tSELECT 1\n"
//        + "\t\tFROM (\n"
//        + "\t\t\tSELECT TABLE_NAME\n"
//        + "\t\t\t\t, CASE \n"
//        + "\t\t\t\t\tWHEN TABLE_TYPE = 'BASE TABLE' THEN 'U'\n"
//        + "\t\t\t\t\tELSE 'V'\n"
//        + "\t\t\t\tEND AS TABLE_XTYPE, TABLE_SCHEMA\n"
//        + "\t\t\tFROM INFORMATION_SCHEMA.TABLES\n"
//        + "\t\t\tWHERE TABLE_SCHEMA = SCHEMA()\n"
//        + "\t\t) KSQL_USERTABLES\n"
//        + "\t\tWHERE TABLE_SCHEMA = SCHEMA()\n"
//        + "\t\t\tAND TABLE_NAME = 'T_GL_MYOPERATION'\n"
//        + "\t) THEN\n"
//        + "\t\tPREPARE stmt FROM 'CREATE TABLE T_GL_MYOPERATION (FID BIGINT(20) NOT NULL, FCREATORID BIGINT(20) NOT NULL DEFAULT 0, FORGID BIGINT(20) NOT NULL DEFAULT 0, FMENUID VARCHAR (36) DEFAULT '' '', FCREATETIME DATETIME, FBIZAPPID VARCHAR (36))';\n"
//        + "\t\tEXECUTE stmt;\n"
//        + "\tEND IF;\n"
//        + "END;\n"
//        + "\n"
//        + "CALL `hahayjb`.`KSQL_TEMP_PROCEDURE_1115493502395`();\n"
//        + "\n"
//        + "DROP PROCEDURE IF EXISTS `hahayjb`.`KSQL_TEMP_PROCEDURE_1115493502395`;";
//    List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, JdbcUtils.MYSQL);
//    String druidsql = SQLUtils.toSQLString(sqlStatements, JdbcUtils.MYSQL);
//    System.out.println(druidsql);
  }

}
