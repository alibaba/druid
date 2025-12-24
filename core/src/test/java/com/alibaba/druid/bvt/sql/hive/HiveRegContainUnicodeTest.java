package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class HiveRegContainUnicodeTest extends TestCase {
    public void test_select() throws Exception {
        String sql = "SELECT page_views.* " +
                "FROM page_views " +
                "WHERE page_views.name REGEXP '[\\u4e00-\\u9fa5]{2,}' and page_views.date >= '2008-03-01'";
        {
            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.hive);
            List<SQLStatement> statementList = parser.parseStatementList();
            String sqlString = SQLUtils.toSQLString(statementList, DbType.hive);
            assertEquals("SELECT page_views.*\n" +
                    "FROM page_views\n" +
                    "WHERE page_views.name REGEXP '[一-龥]{2,}'\n" +
                    "\tAND page_views.date >= '2008-03-01'", sqlString);
        }

        {
            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.hive);
            parser.config(SQLParserFeature.KeepUnicodeEscape, true);
            List<SQLStatement> statementList = parser.parseStatementList();
            String sqlString = SQLUtils.toSQLString(statementList, DbType.hive);

            assertEquals("SELECT page_views.*\n" +
                    "FROM page_views\n" +
                    "WHERE page_views.name REGEXP '[\\u4e00-\\u9fa5]{2,}'\n" +
                    "\tAND page_views.date >= '2008-03-01'", sqlString);
        }

        {
            String fotmat = SQLUtils.format(sql, DbType.hive, null, null, new SQLParserFeature[]{SQLParserFeature.KeepUnicodeEscape});

            assertEquals("SELECT page_views.*\n" +
                    "FROM page_views\n" +
                    "WHERE page_views.name REGEXP '[\\u4e00-\\u9fa5]{2,}'\n" +
                    "\tAND page_views.date >= '2008-03-01'", fotmat);
        }

    }
}
