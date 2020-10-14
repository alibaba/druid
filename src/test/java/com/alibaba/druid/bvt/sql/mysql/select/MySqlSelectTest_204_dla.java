package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLAdhocTableSource;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.FnvHash;

import java.util.ArrayList;
import java.util.List;

public class MySqlSelectTest_204_dla extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT temp_1.$1, temp_2.smallint_col\n" +
                "FROM \n" +
                "TABLE temp_1\n" +
                "(\n" +
                "  $1 int,\n" +
                "  $2 int\n" +
                ")\n" +
                "TBLPROPERTIES (\n" +
                "  TYPE='oss',\n" +
                "  LOCATION='oss//x.x.x.x:xxx/test_db',\n" +
                "  SCHEMA='test_db'\n" +
                ")\n" +
                "META LIFECYCLE 1\n" +
                "\n" +
                "JOIN\n" +
                "\n" +
                "TABLE temp_2\n" +
                "(\n" +
                "    id INT COMMENT 'default',\n" +
                "    bool_col BOOLEAN COMMENT 'default',\n" +
                "    tinyint_col TINYINT COMMENT 'default',\n" +
                "    smallint_col SMALLINT COMMENT 'default',\n" +
                "    int_col INT COMMENT 'default',\n" +
                "    bigint_col BIGINT COMMENT 'default',\n" +
                "    float_col FLOAT COMMENT 'default',\n" +
                "    double_col DOUBLE COMMENT 'default',\n" +
                "    date_string_col STRING COMMENT 'default',\n" +
                "    string_col STRING COMMENT 'default',\n" +
                "    timestamp_col TIMESTAMP COMMENT 'default'\n" +
                ")\n" +
                "ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe' \n" +
                "WITH SERDEPROPERTIES ('field.delim'='|', 'serialization.format'='|') \n" +
                "STORED AS INPUTFORMAT 'org.apache.hadoop.mapred.TextInputFormat'\n" +
                "OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'\n" +
                "LOCATION 'oss://xxx/xxx/xxx.csv'\n" +
                "TBLPROPERTIES ('recursive.directories'='false')\n" +
                "META LIFECYCLE 1\n" +
                "\n" +
                "ON temp_1.$1 = temp_2.id\n" +
                "WHERE temp_2.bool_col = true;";

//        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT temp_1.$1, temp_2.smallint_col\n" +
                "FROM TABLE temp_1 (\n" +
                "\t$1 int,\n" +
                "\t$2 int\n" +
                ")\n" +
                "TBLPROPERTIES (\n" +
                "\t'TYPE' = 'oss',\n" +
                "\t'LOCATION' = 'oss//x.x.x.x:xxx/test_db',\n" +
                "\t'SCHEMA' = 'test_db'\n" +
                ")\n" +
                "META LIFECYCLE 1\n" +
                "\tJOIN TABLE temp_2 (\n" +
                "\t\tid INT COMMENT 'default',\n" +
                "\t\tbool_col BOOLEAN COMMENT 'default',\n" +
                "\t\ttinyint_col TINYINT COMMENT 'default',\n" +
                "\t\tsmallint_col SMALLINT COMMENT 'default',\n" +
                "\t\tint_col INT COMMENT 'default',\n" +
                "\t\tbigint_col BIGINT COMMENT 'default',\n" +
                "\t\tfloat_col FLOAT COMMENT 'default',\n" +
                "\t\tdouble_col DOUBLE COMMENT 'default',\n" +
                "\t\tdate_string_col STRING COMMENT 'default',\n" +
                "\t\tstring_col STRING COMMENT 'default',\n" +
                "\t\ttimestamp_col TIMESTAMP COMMENT 'default'\n" +
                "\t)\n" +
                "\tROW FORMAT\n" +
                "\t\tSERDE 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'\n" +
                "\tWITH SERDEPROPERTIES (\n" +
                "\t\t'field.delim' = '|',\n" +
                "\t\t'serialization.format' = '|'\n" +
                "\t)\n" +
                "\tSTORED AS\n" +
                "\t\tINPUTFORMAT 'org.apache.hadoop.mapred.TextInputFormat'\n" +
                "\t\tOUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'\n" +
                "\tLOCATION 'oss://xxx/xxx/xxx.csv'\n" +
                "\tTBLPROPERTIES (\n" +
                "\t\t'recursive.directories' = 'false'\n" +
                "\t)\n" +
                "\tMETA LIFECYCLE 1 ON temp_1.$1 = temp_2.id\n" +
                "WHERE temp_2.bool_col = true;", stmt.toString());

        assertEquals("select temp_1.$1, temp_2.smallint_col\n" +
                "from table temp_1 (\n" +
                "\t$1 int,\n" +
                "\t$2 int\n" +
                ")\n" +
                "tblproperties (\n" +
                "\t'TYPE' = 'oss',\n" +
                "\t'LOCATION' = 'oss//x.x.x.x:xxx/test_db',\n" +
                "\t'SCHEMA' = 'test_db'\n" +
                ")\n" +
                "meta lifecycle 1\n" +
                "\tjoin table temp_2 (\n" +
                "\t\tid INT comment 'default',\n" +
                "\t\tbool_col BOOLEAN comment 'default',\n" +
                "\t\ttinyint_col TINYINT comment 'default',\n" +
                "\t\tsmallint_col SMALLINT comment 'default',\n" +
                "\t\tint_col INT comment 'default',\n" +
                "\t\tbigint_col BIGINT comment 'default',\n" +
                "\t\tfloat_col FLOAT comment 'default',\n" +
                "\t\tdouble_col DOUBLE comment 'default',\n" +
                "\t\tdate_string_col STRING comment 'default',\n" +
                "\t\tstring_col STRING comment 'default',\n" +
                "\t\ttimestamp_col TIMESTAMP comment 'default'\n" +
                "\t)\n" +
                "\trow rowFormat\n" +
                "\t\tserde 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'\n" +
                "\twith serdeproperties (\n" +
                "\t\t'field.delim' = '|',\n" +
                "\t\t'serialization.format' = '|'\n" +
                "\t)\n" +
                "\tstored as\n" +
                "\t\tinputformat 'org.apache.hadoop.mapred.TextInputFormat'\n" +
                "\t\toutputformat 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'\n" +
                "\tlocation 'oss://xxx/xxx/xxx.csv'\n" +
                "\ttblproperties (\n" +
                "\t\t'recursive.directories' = 'false'\n" +
                "\t)\n" +
                "\tmeta lifecycle 1 on temp_1.$1 = temp_2.id\n" +
                "where temp_2.bool_col = true;", stmt.toLowerCaseString());

        final TempTableNameGen tempTableNameGen = new TempTableNameGen() {
            @Override
            public String generateName() {
                return "__temp_table_1";
            }
        };

        final List<SQLCreateTableStatement> createTableStatementList = new ArrayList<SQLCreateTableStatement>();
        SQLASTVisitorAdapter v = new MySqlASTVisitorAdapter() {
            public boolean visit(SQLAdhocTableSource x) {
                final String tableName = tempTableNameGen.generateName();

                HiveCreateTableStatement createStmt = (HiveCreateTableStatement) x.getDefinition();
                createStmt.setParent(null);
                createStmt.setTableName(tableName);
                createStmt.setExternal(true);

                SQLUtils.replaceInParent(x, new SQLExprTableSource(tableName));
                createTableStatementList.add(createStmt);
                return false;
            }

            public boolean visit(SQLVariantRefExpr x) {
                String name = x.getName();
                if (name != null && name.startsWith("$")) {
                    SQLUtils.replaceInParent(x, new SQLIdentifierExpr(name));
                }
                return false;
            }
        };
        stmt.accept(v);

        for (SQLCreateTableStatement createStmt : createTableStatementList) {
            System.out.println(createStmt.toString(VisitorFeature.OutputNameQuote));
        }

        System.out.println();
        System.out.println(stmt.toString(VisitorFeature.OutputNameQuote));


        HiveCreateTableStatement createTableStatement = (HiveCreateTableStatement) createTableStatementList.get(0);
        SQLExpr lifeCycle = createTableStatement.getMetaLifeCycle();
        if (lifeCycle instanceof SQLIntegerExpr) {
            int intValue = ((SQLIntegerExpr) lifeCycle).getNumber().intValue();
        } else if (lifeCycle instanceof SQLIdentifierExpr && ((SQLIdentifierExpr) lifeCycle).nameHashCode64() == FnvHash.Constants.ALWAYS) {
            // always
        }
        //
    }

    static interface TempTableNameGen {
        String generateName();
    }
}