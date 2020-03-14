package com.alibaba.druid.bvt.sql.phoenix;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.phoenix.parser.PhoenixStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by dove on 20/3/14.
 */
public class PhoenixCreateDDLTest extends TestCase {

    /**
     * http://phoenix.apache.org/language/index.html#create_table
     */
    private static String inputSql = "CREATE TABLE IF NOT EXISTS TESTDW.PHOENIX_TB_BY_DOVE (\n" +
            "id bigint(20) NOT NULL,\n" +
            "age bigint(20) NOT NULL,\n" +
            "create_time date,\n" +
            "price double(10,3),\n" +
            "product_name varchar,\n" +
            "update_time timestamp,\n" +
            "CONSTRAINT PHOENIX_TB_BY_DOVE PRIMARY KEY (ID DESC,age ASC)\n" +
            ") SALT_BUCKETS = 12,\n" +
            "DISABLE_WAL=false,\n" +
            "IMMUTABLE_ROWS=false,\n" +
            "MULTI_TENANT=false,\n" +
            "DEFAULT_COLUMN_FAMILY='a',\n" +
            "STORE_NULLS=false,\n" +
            "TRANSACTIONAL=false,\n" +
            "UPDATE_CACHE_FREQUENCY=3000,\n" +
            "APPEND_ONLY_SCHEMA=false,\n" +
            "AUTO_PARTITION_SEQ=id,\n" +
            "GUIDE_POSTS_WIDTH=3000,\n" +
            "COMPRESSION='SNAPPY'";

    public static void test_0() {
        String tableName = null;
        List<SQLStatement> listSqlStatement = SQLUtils.parseStatements(inputSql, JdbcConstants.PHOENIX);
        SchemaStatVisitor visitor = new SchemaStatVisitor(JdbcConstants.PHOENIX);
        if (listSqlStatement.size() != 1) {
            return;
        }
        listSqlStatement.get(0).accept(visitor);
        Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
        Iterator<TableStat.Name> m = tableMap.keySet().iterator();
        if (m.hasNext()) {
            tableName = m.next().getName();
        }
        Assert.assertEquals("TESTDW.PHOENIX_TB_BY_DOVE", tableName);
    }

    public static void test_1() {
        PhoenixStatementParser parser = new PhoenixStatementParser(inputSql.replaceAll("\\n", ""));
        String tableName = parser.getSQLCreateTableParser().parseCreateTable().getTableSource().toString();
        Assert.assertEquals("TESTDW.PHOENIX_TB_BY_DOVE", tableName);
    }

}
