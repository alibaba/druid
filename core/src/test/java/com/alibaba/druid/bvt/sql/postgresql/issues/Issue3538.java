package com.alibaba.druid.bvt.sql.postgresql.issues;

import java.util.List;
import java.util.Map;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.SQLUtils.FormatOption;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Name;
import com.alibaba.druid.wall.WallUtils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @see <a href="https://www.postgresql.org/docs/current/sql-start-transaction.html">start transaction语法</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-begin.html">begin语法</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-end.html">end语法</a>
 */
public class Issue3538 {
    @Test
    public void test_end() throws Exception {
        DbType dbType= DbType.postgresql;
        String sql = "end;\nend;";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> statements = parser.parseStatementList();
        assertEquals(2, statements.size());
        FormatOption DEFAULT_FORMAT_OPTION = new FormatOption(false, true, false);
        for(SQLStatement statement : statements) {
            System.out.println("sql: " + statement.getClass().getName() + " " + statement.toString());
            String result = SQLUtils.toSQLString(statement, dbType, DEFAULT_FORMAT_OPTION);
            System.out.println(result);
        }
    }
    @Test
    public void test_begin_end() throws Exception {
        DbType dbType= DbType.postgresql;
        String sql = "begin;\n"
            + "update table set name='a', sn='a' where id=1;\n"
            + "update table set name='b', sn='b' where id=2;\n"
            + "update table set name='c', sn='c' where id=3;\n"
            + "end;";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> statements = parser.parseStatementList();
        assertEquals(5, statements.size());
        com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGUpdateStatement jjj;
        FormatOption DEFAULT_FORMAT_OPTION = new FormatOption(false, true, false);
        for(SQLStatement statement : statements) {
            System.out.println("sql: " + statement.getClass().getName() + " " + statement.toString());
            String result = SQLUtils.toSQLString(statement, dbType, DEFAULT_FORMAT_OPTION);
            System.out.println(result);
        }
    }
    @Test
    public void test_start_end() throws Exception {
        String sql = "start transaction;\n"
            + "update table set name='a', sn='a' where id=1;\n"
            + "update table set name='b', sn='b' where id=2;\n"
            + "update table set name='c', sn='c' where id=3;\n"
            + "end;";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.postgresql);
        List<SQLStatement> statements = parser.parseStatementList();
        assertEquals(5, statements.size());
        FormatOption DEFAULT_FORMAT_OPTION = new FormatOption(false, true, false);
        for(SQLStatement statement : statements) {
            System.out.println("sql: " + statement.getClass().getName() + " " + statement.toString());
            String result = SQLUtils.toSQLString(statement, DbType.postgresql, DEFAULT_FORMAT_OPTION);
            System.out.println(result);
        }
    }
}
