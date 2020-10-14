package com.alibaba.druid.demo.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;
import java.util.Set;

public class CreateTableSetSchemaDemo extends TestCase {

    public void test_schemaStat() throws Exception {
String sql = "create table t(fid varchar(20))";

DbType dbType = DbType.oracle;
SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
List<SQLStatement> stmtList = parser.parseStatementList();

SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(dbType);
for (SQLStatement stmt : stmtList) {
    SQLCreateTableStatement createTable = ((SQLCreateTableStatement) stmt);
    createTable.setSchema("sc001");
}

String sql2 = SQLUtils.toSQLString(stmtList, DbType.oracle);
System.out.println(sql2);
    }
}
