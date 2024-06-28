package com.alibaba.druid.bvt.sql.mysql.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5894>Issue来源</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/cast-functions.html#function_cast">CAST(expr AS type [ARRAY])</a>
 */
public class Issue5894 {

    @Test
    public void test_parse_aschararray() {
        for (DbType dbType : new DbType[]{DbType.mysql}) {
            for (String sql : new String[]{
                "alter table db1.rs_push_mall_data add key idx_bill_no_json((CAST(bill_no_json AS CHAR(50) ARRAY)));",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> statementList = parser.parseStatementList();
                assertEquals(1, statementList.size());
                assertEquals("ALTER TABLE db1.rs_push_mall_data\n"
                    + "\tADD KEY idx_bill_no_json ((CAST(bill_no_json AS CHAR(50) ARRAY)));", statementList.get(0).toString());
                SQLParseAssertUtil.assertParseSql(sql, dbType);
            }
        }
    }
}
