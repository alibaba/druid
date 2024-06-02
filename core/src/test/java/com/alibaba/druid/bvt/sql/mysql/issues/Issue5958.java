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
 * @see <a href="https://github.com/alibaba/druid/issues/5958" >Issue来源</a>
 */
public class Issue5958 {


    @Test
    public void test_parse_alter() {
        for (DbType dbType : new DbType[]{DbType.mysql}) {
            for (String sql : new String[]{
                //"select (((a-b))) from aa;",
                "alter TABLE test.rs_urge_pickup_config ADD KEY idx_site_id_list2 ((cast(site_id_list as char(10) array)));",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> statementList = parser.parseStatementList();
                System.out.println(statementList);
                assertEquals(1, statementList.size());
                SQLParseAssertUtil.assertParseSql(sql, dbType);
            }
        }
    }
}
