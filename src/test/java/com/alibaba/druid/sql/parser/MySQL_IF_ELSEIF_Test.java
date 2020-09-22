package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class MySQL_IF_ELSEIF_Test extends TestCase {

    public void test_IF_ELSEIF(){
        String sql = "CREATE TRIGGER CSRC_JG_GSGDXX BEFORE INSERT ON test_table_name FOR EACH ROW\n" +
                "begin\n" +
                "\tIF new.BSRQ IS NOT NULL THEN\n" +
                "\t\tset new.SJRQ1 = new.BSRQ;\n" +
                "\tELSEIF  new.SJRQ1 IS NOT NULL THEN\n" +
                "\t\tset new.BSRQ = new.SJRQ1;\n" +
                "\t\tset  new.SJRQ2 =new.SJRQ1;\n" +
                "    ELSEIF  new.SJRQ2  IS NOT NULL THEN\n" +
                "\t\tset new.SJRQ1 =new.SJRQ2;\n" +
                "\tEND IF;\n" +
                "end;";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        StringBuilder out = new StringBuilder();
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmtList.get(0).accept(visitor);
        System.out.println(stmtList.get(0).toString());
    }
}
