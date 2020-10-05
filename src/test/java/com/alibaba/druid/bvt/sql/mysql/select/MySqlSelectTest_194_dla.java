package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlSelectTest_194_dla extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT id, bool_col FROM\n" +
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
                "TBLPROPERTIES ('recursive.directories'='false');\n";

//        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT id, bool_col\n" +
                "FROM TABLE temp_2 (\n" +
                "\tid INT COMMENT 'default',\n" +
                "\tbool_col BOOLEAN COMMENT 'default',\n" +
                "\ttinyint_col TINYINT COMMENT 'default',\n" +
                "\tsmallint_col SMALLINT COMMENT 'default',\n" +
                "\tint_col INT COMMENT 'default',\n" +
                "\tbigint_col BIGINT COMMENT 'default',\n" +
                "\tfloat_col FLOAT COMMENT 'default',\n" +
                "\tdouble_col DOUBLE COMMENT 'default',\n" +
                "\tdate_string_col STRING COMMENT 'default',\n" +
                "\tstring_col STRING COMMENT 'default',\n" +
                "\ttimestamp_col TIMESTAMP COMMENT 'default'\n" +
                ")\n" +
                "ROW FORMAT\n" +
                "\tSERDE 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'\n" +
                "WITH SERDEPROPERTIES (\n" +
                "\t'field.delim' = '|',\n" +
                "\t'serialization.format' = '|'\n" +
                ")\n" +
                "STORED AS\n" +
                "\tINPUTFORMAT 'org.apache.hadoop.mapred.TextInputFormat'\n" +
                "\tOUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'\n" +
                "LOCATION 'oss://xxx/xxx/xxx.csv'\n" +
                "TBLPROPERTIES (\n" +
                "\t'recursive.directories' = 'false'\n" +
                ");", stmt.toString());

        assertEquals("select id, bool_col\n" +
                "from table temp_2 (\n" +
                "\tid INT comment 'default',\n" +
                "\tbool_col BOOLEAN comment 'default',\n" +
                "\ttinyint_col TINYINT comment 'default',\n" +
                "\tsmallint_col SMALLINT comment 'default',\n" +
                "\tint_col INT comment 'default',\n" +
                "\tbigint_col BIGINT comment 'default',\n" +
                "\tfloat_col FLOAT comment 'default',\n" +
                "\tdouble_col DOUBLE comment 'default',\n" +
                "\tdate_string_col STRING comment 'default',\n" +
                "\tstring_col STRING comment 'default',\n" +
                "\ttimestamp_col TIMESTAMP comment 'default'\n" +
                ")\n" +
                "row rowFormat\n" +
                "\tserde 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'\n" +
                "with serdeproperties (\n" +
                "\t'field.delim' = '|',\n" +
                "\t'serialization.format' = '|'\n" +
                ")\n" +
                "stored as\n" +
                "\tinputformat 'org.apache.hadoop.mapred.TextInputFormat'\n" +
                "\toutputformat 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'\n" +
                "location 'oss://xxx/xxx/xxx.csv'\n" +
                "tblproperties (\n" +
                "\t'recursive.directories' = 'false'\n" +
                ");", stmt.toLowerCaseString());
    }
}