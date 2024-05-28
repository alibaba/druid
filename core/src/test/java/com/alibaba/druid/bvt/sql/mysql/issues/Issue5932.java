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
 * @see <a href="https://github.com/alibaba/druid/issues/5932" >Issue来源</a>
 */
public class Issue5932 {


    @Test
    public void test_parse_selectsum() {
        for (DbType dbType : new DbType[]{DbType.mysql}) {
            for (String sql : new String[]{
                "SELECT a.aab001, COUNT(DISTINCT a.aac001) AS 住院人数, SUM(a.bka030) AS 住院天数\n" +
                    "\t, SUM((\n" +
                    "\t\tSELECT SUM(b.aae019)\n" +
                    "\t\tFROM kc27 b\n" +
                    "\t\tWHERE b.aaa157 IN ('350100','350300','350806')\n" +
                    "\t\t\tAND a.AKB020 = b.AKB020\n" +
                    "\t        AND a.AAZ217 = b.AAZ217\n" +
                    "\t        AND b.AAE100 = '1'\n" +
                    "\t)) AS '住院记账医疗费用'\n" +
                    "FROM kc21 a\n" +
                    "WHERE a.aae100 = '1'\n" +
                    "\tAND a.aka130 = '62'\n" +
                    "\tAND a.BAA027 = '440111'\n" +
                    "\tAND a.AKC194 >= DATE_FORMAT('20240401', '%Y-%m-%d %T')\n" +
                    "\tAND a.AKC194 <  DATE_FORMAT('20240501', '%Y-%m-%d %T')\n" +
                    "\tGROUP BY a.aab001;",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> statementList = parser.parseStatementList();
                System.out.println(statementList);
                assertEquals(1, statementList.size());
                assertEquals("SELECT a.aab001, COUNT(DISTINCT a.aac001) AS 住院人数, SUM(a.bka030) AS 住院天数\n"
                    + "\t, SUM((\n"
                    + "\t\tSELECT SUM(b.aae019)\n"
                    + "\t\tFROM kc27 b\n"
                    + "\t\tWHERE b.aaa157 IN ('350100', '350300', '350806')\n"
                    + "\t\t\tAND a.AKB020 = b.AKB020\n"
                    + "\t\t\tAND a.AAZ217 = b.AAZ217\n"
                    + "\t\t\tAND b.AAE100 = '1'\n"
                    + "\t)) AS \"住院记账医疗费用\"\n"
                    + "FROM kc21 a\n"
                    + "WHERE a.aae100 = '1'\n"
                    + "\tAND a.aka130 = '62'\n"
                    + "\tAND a.BAA027 = '440111'\n"
                    + "\tAND a.AKC194 >= DATE_FORMAT('20240401', '%Y-%m-%d %T')\n"
                    + "\tAND a.AKC194 < DATE_FORMAT('20240501', '%Y-%m-%d %T')\n"
                    + "GROUP BY a.aab001;", statementList.get(0).toString());
                SQLParseAssertUtil.assertParseSql(sql, dbType);
            }
        }
    }
}
