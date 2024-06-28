package com.alibaba.druid.bvt.sql.mysql.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5908" >Issue来源</a>
 */
public class Issue5908 {

    @Test
    public void test_parse_brackets() {
        for (DbType dbType : new DbType[]{DbType.mysql}) {
            for (String sql : new String[]{
                "select array_length(['1'])", // https://github.com/alibaba/druid/issues/5743
                "SELECT CONCAT(date_id, ' ', e1, e2) hour_minute1 \n"
                    + "from (select 1 k1) AS t,\n"
                    + "(SELECT date_id FROM dim_date \n"
                    + "WHERE date_id between date_format(DATE_SUB(20240513, INTERVAL 7 DAY), 'yyyy-MM-dd') AND date_format(20240513, 'yyyy-MM-dd')) dd\n"
                    + "lateral VIEW explode(['00', '01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23']) tmp1 as e1\n"
                    + "lateral VIEW explode([':00:00', ':30:00']) tmp2 AS e2;",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> statementList = parser.parseStatementList();
                System.out.println(statementList);
                assertEquals(1, statementList.size());
                SQLParseAssertUtil.assertParseSql(sql, dbType);
            }
        }
    }

    @Test
    public void test_parse_brackets2() {
        for (DbType dbType : new DbType[]{DbType.postgresql}) {
            for (String sql : new String[]{
                "SELECT t.name AS 用户（一类）名称 FROM user_table t",
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
