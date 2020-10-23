package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest126 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE tablename1 (\n"
                     + "      id INT,\n"
                     + "      title clob,\n"
                     + "      body clob,\n"
                     + "      comment clob,\n"
                     + "      author clob,\n"
                     + "      FULLTEXT INDEX f_idx1(title) WITH INDEX ANALYZER 'analyzer_name1' WITH QUERY ANALYZER 'analyzer_name2',\n"
                     + "      FULLTEXT INDEX f_idx2(body) WITH ANALYZER 'analyzer_name3',    "
                     + "      FULLTEXT INDEX f_idx3(comment) WITH INDEX ANALYZER 'analyzer_name4',  "
                     + "      FULLTEXT INDEX f_idx4(author)  WITH QUERY ANALYZER 'analyzer_name5',"
                     + "      FULLTEXT INDEX f_idx5(author)\n"
                     + ");";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE tablename1 (\n" + "\tid INT,\n" + "\ttitle clob,\n" + "\tbody clob,\n"
                     + "\tcomment clob,\n" + "\tauthor clob,\n"
                     + "\tFULLTEXT INDEX f_idx1(title) WITH INDEX ANALYZER 'analyzer_name1' WITH QUERY ANALYZER 'analyzer_name2',\n"
                     + "\tFULLTEXT INDEX f_idx2(body) WITH ANALYZER 'analyzer_name3',\n"
                     + "\tFULLTEXT INDEX f_idx3(comment) WITH INDEX ANALYZER 'analyzer_name4',\n"
                     + "\tFULLTEXT INDEX f_idx4(author) WITH QUERY ANALYZER 'analyzer_name5',\n"
                     + "\tFULLTEXT INDEX f_idx5(author)\n" + ");", stmt.toString());

    }



}