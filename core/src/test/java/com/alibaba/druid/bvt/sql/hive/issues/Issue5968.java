package com.alibaba.druid.bvt.sql.hive.issues;

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
 * @see <a href="https://github.com/alibaba/druid/issues/5968" >Issue来源</a>
 * @see <a href="https://cwiki.apache.org/confluence/display/Hive/LanguageManual+DDL">Hive DDL</a>
 */
public class Issue5968 {


    @Test
    public void test_parse_stored_by() {
        for (DbType dbType : new DbType[]{DbType.hive}) {
            for (String sql : new String[]{

                "CREATE TABLE IF NOT EXISTS test.tests(\n"
                    + "  id bigint COMMENT 'from deserializer',\n "
                    + "  job_group bigint COMMENT 'from deserializer',\n"
                    + "  job_id bigint COMMENT 'from deserializer',\n"
                    + "  ums_ts_ bigint COMMENT 'from deserializer')\n"
                    + " comment 'test' \n"
                    + " ROW FORMAT SERDE  "
                    + " 'org.apache.paimon.hive.PaimonSerDe'\n "
                    + "  STORED BY "
                    + " 'org.apache.paimon.hive.PaimonStorageHandler';",
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
