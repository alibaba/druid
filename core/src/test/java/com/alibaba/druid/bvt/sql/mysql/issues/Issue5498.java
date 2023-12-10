package com.alibaba.druid.bvt.sql.mysql.issues;

import java.util.Map;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * 验证 select into 语句的解析
 *
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5498">Issue来源</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/select-into.html">MySQL select into语法</a>
 */
public class Issue5498 {

    @Test
    public void test_select_into() throws Exception {
        for (DbType dbType : new DbType[]{DbType.mysql, DbType.oracle}) {
            for (String sql : new String[]{
                "select a,b into c,d from test",
                "select a,b into (c,d) from test",
                "select a into c from test",
                "select a,b,c into d,e,f from test",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                SQLStatement statement = parser.parseStatement();
                System.out.println(dbType + "原始的sql===" + sql);
                System.out.println(dbType + "归一化的sql===" + Issue5421.normalizeSql(sql));
                String newSql = statement.toString() + ";";
                System.out.println(dbType + "生成的sql归一化===" + Issue5421.normalizeSql(newSql));
                parser = SQLParserUtils.createSQLStatementParser(newSql, dbType);
                statement = parser.parseStatement();
                System.out.println(dbType + "再次解析对象得到sql===" + Issue5421.normalizeSql(statement.toString()));
            }
        }
    }
}
