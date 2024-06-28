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
 * @see <a href="https://github.com/alibaba/druid/issues/5362>Issue来源</a>
 */
public class Issue5362 {

    @Test
    public void test_parse_asorder() {
        for (DbType dbType : DbType.values()) {
            for (String sql : new String[]{
                "select b.order + 1 as order from book b",
            }) {
                SQLParseAssertUtil.assertParseSql(sql, dbType);
            }
        }
    }
}
