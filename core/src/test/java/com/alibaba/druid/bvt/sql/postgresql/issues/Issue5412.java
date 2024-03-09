package com.alibaba.druid.bvt.sql.postgresql.issues;

import java.util.List;
import java.util.Map;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.wall.WallUtils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * 验证 SQLUtils.parseStatements解析PG和GP时不支持analyze和vacuum #5412
 *
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5412">修复 #5412</a>
 */
public class Issue5412 {

    @Test
    public void test_analyze_emptytable() throws Exception {
        for (DbType dbType : new DbType[]{DbType.postgresql, DbType.greenplum, DbType.edb}) {
            for (String sql : new String[]{
                "analyze ",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                SQLStatement statement = parser.parseStatement();
                System.out.println(dbType + "原始的sql===" + sql);
                System.out.println(dbType + "生成的sql===" + statement);
                SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
                statement.accept(visitor);
                System.out.println(dbType + "getTables==" + visitor.getTables());
                Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
                assertTrue(tableMap.isEmpty());
            }

        }
    }

    @Test
    public void test_analyze() throws Exception {
        for (DbType dbType : new DbType[]{DbType.postgresql, DbType.greenplum, DbType.edb}) {

            for (String sql : new String[]{
                "analyze WORK.TABLE1;",
                "analyze WORK.TABLE1,WORK.TABLE2;",
                "analyze VERBOSE WORK.TABLE1,WORK.TABLE2;",
                "analyze VERBOSE SKIP_LOCKED WORK.TABLE1;",
                "analyze SKIP_LOCKED WORK.TABLE1;",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                SQLStatement statement = parser.parseStatement();
                System.out.println(dbType + "原始的sql===" + sql);
                System.out.println(dbType + "生成的sql===" + statement);
                SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
                statement.accept(visitor);
                System.out.println(dbType + "getTables==" + visitor.getTables());
                Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
                assertFalse(tableMap.isEmpty());
            }

        }
    }

    @Test
    public void test_vacuum_empty() throws Exception {
        for (DbType dbType : new DbType[]{DbType.postgresql, DbType.greenplum, DbType.edb}) {

            for (String sql : new String[]{
                "vacuum ",
                "vacuum   ;vacuum ",
                "vacuum;vacuum;vacuum bb;",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> statementList = parser.parseStatementList();
                System.out.println(dbType + "原始的sql===" + sql);
                System.out.println(dbType + "生成的sql===" + statementList);
                SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
                statementList.get(0).accept(visitor);
                Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
                assertTrue(tableMap.isEmpty());
            }
        }
    }

    @Test
    public void test_vacuum() throws Exception {
        for (DbType dbType : new DbType[]{DbType.postgresql, DbType.greenplum, DbType.edb}) {
            for (String sql : new String[]{
                "vacuum WORK.TABLE1;",
                "vacuum WORK.TABLE1,WORK.TABLE2;",
                "vacuum VERBOSE WORK.TABLE1,WORK.TABLE2;",
                "vacuum VERBOSE FULL WORK.TABLE1,WORK.TABLE2;",
                "vacuum VERBOSE FREEZE WORK.TABLE1,WORK.TABLE2,WORK.TABLE3;",
                "vacuum VERBOSE ANALYZE SKIP_LOCKED PROCESS_TOAST  WORK.TABLE1;",
                "vacuum SKIP_LOCKED TRUNCATE WORK.TABLE1;",
                "VACUUM FULL FREEZE VERBOSE ANALYZE DISABLE_PAGE_SKIPPING SKIP_LOCKED PROCESS_TOAST TRUNCATE WORK.TABLE3;",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                SQLStatement statement = parser.parseStatement();
                System.out.println(dbType + "原始的sql===" + sql);
                System.out.println(dbType + "生成的sql===" + statement);
                SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
                statement.accept(visitor);
                System.out.println(dbType + "getTables==" + visitor.getTables());
                Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
                assertFalse(tableMap.isEmpty());
            }

        }
    }
}
