package com.alibaba.druid.bvt.sql.postgresql.issues;

import java.util.Map;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * 验证 Postgresql 无法解析 alter database 语句 #5444
 *
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5444">增强 #5444</a>
 */
public class Issue5444 {

    @Test
    public void test_alterdatabase() throws Exception {
        for (DbType dbType : new DbType[]{DbType.postgresql}) {
            for (String sql : new String[]{
                "alter database 'aaa' rename to ccc;",
                "ALTER DATABASE test SET enable_indexscan = off;",
                "ALTER DATABASE test SET enable_indexscan TO off;",
                "alter database 'aaa' owner to ddd;",
                "ALTER DATABASE test SET TABLESPACE new_tablespace;",
                "ALTER DATABASE test REFRESH COLLATION VERSION;",
                "ALTER DATABASE test RESET ALL;",
                "ALTER DATABASE test RESET abcdd;",
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
}
