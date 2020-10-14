package com.alibaba.druid.bvt.sql.mysql.createTable;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

public class MySqlCreateTableTest133_drds_gsi extends MysqlTest {

    // Constraint XXX may not show in parsed sql, so removed it.
    private static final String       CREATE_TABLE_BASE    = "CREATE TABLE IF NOT EXISTS `full_type_table` (\n"
                                                             + "\tpk INT NOT NULL PRIMARY KEY AUTO_INCREMENT,\n"
                                                             + "\tid1 INT,\n" + "\tid2 INT,\n"
                                                             + "\tid3 VARCHAR(100),\n" + "\tvc1 VARCHAR(100),\n"
                                                             + "\tvc3 VARCHAR(100),\n"
                                                             + "\tINDEX idx1 USING HASH(id1),\n"
                                                             + "\tKEY idx2 USING HASH (id2),\n"
                                                             + "\tFULLTEXT KEY idx4 (id3(20)),\n"
                                                             + "\tUNIQUE idx3 USING BTREE (vc1(20))";
    private static final String       CREATE_TABLE_TAIL    = "\n) ENGINE = INNODB AUTO_INCREMENT = 2 AVG_ROW_LENGTH = 100 "
                                                             + "CHARACTER SET = utf8 COLLATE = utf8_bin CHECKSUM = 0 COMMENT 'abcd'\n"
                                                             + "DBPARTITION BY HASH(id1);";

    private static final String       FULL_TYPE_TABLE      = CREATE_TABLE_BASE + CREATE_TABLE_TAIL;

    private static final List<String> GSI_DEFINITIONS      = new ArrayList<String>();

    private static final List<String> GSI_DEF_HEAD         = new ArrayList<String>();
    private static final List<String> GSI_DEF_COLUMN_DB    = new ArrayList<String>();
    private static final List<String> GSI_DEF_COLUMN_TB    = new ArrayList<String>();
    private static final List<String> GSI_DEF_SHARDING_DB  = new ArrayList<String>();
    private static final List<String> GSI_DEF_SHARDING_TB  = new ArrayList<String>();
    private static final List<String> GSI_DEF_INDEX_OPTION = new ArrayList<String>();

    static {

        GSI_DEF_HEAD.add("GLOBAL INDEX");
        GSI_DEF_HEAD.add("UNIQUE GLOBAL");
        // GSI_DEF_HEAD.add("GLOBAL UNIQUE INDEX");

        GSI_DEF_COLUMN_DB.add("gsi_id2(id2)");
        GSI_DEF_COLUMN_DB.add("gsi_id2(id2) COVERING (vc1)");
        GSI_DEF_COLUMN_DB.add("gsi_id2(id2) COVERING (vc1, vc2)");

        GSI_DEF_COLUMN_TB.add("gsi_id2(id2, id3)");
        GSI_DEF_COLUMN_TB.add("gsi_id2(id2, id3) COVERING (vc1)");
        GSI_DEF_COLUMN_TB.add("gsi_id2(id2, id3) COVERING (vc1, vc2)");
        GSI_DEF_COLUMN_TB.add("gsi_id2 USING HASH(id2, id3) COVERING (vc1, vc2)");

        GSI_DEF_SHARDING_DB.add("DBPARTITION BY HASH(id2)");
        GSI_DEF_SHARDING_DB.add("DBPARTITION BY HASH(id2) TBPARTITION BY HASH(id2) TBPARTITIONS 3");
        GSI_DEF_SHARDING_DB.add("DBPARTITION BY HASH(id2) TBPARTITION BY MM(id2) TBPARTITIONS 3");
        GSI_DEF_SHARDING_DB.add("DBPARTITION BY HASH(id2) TBPARTITION BY DD(id2) TBPARTITIONS 3");
        GSI_DEF_SHARDING_DB.add("DBPARTITION BY HASH(id2) TBPARTITION BY WEEK(id2) TBPARTITIONS 3");
        GSI_DEF_SHARDING_DB.add("DBPARTITION BY HASH(id2) TBPARTITION BY MMDD(id2) TBPARTITIONS 3");

        GSI_DEF_SHARDING_TB.add("DBPARTITION BY HASH(id2) TBPARTITION BY HASH(id3) TBPARTITIONS 3");
        GSI_DEF_SHARDING_TB.add("DBPARTITION BY HASH(id2) TBPARTITION BY MM(id3) TBPARTITIONS 3");
        GSI_DEF_SHARDING_TB.add("DBPARTITION BY HASH(id2) TBPARTITION BY DD(id3) TBPARTITIONS 3");
        GSI_DEF_SHARDING_TB.add("DBPARTITION BY HASH(id2) TBPARTITION BY WEEK(id3) TBPARTITIONS 3");
        GSI_DEF_SHARDING_TB.add("DBPARTITION BY HASH(id2) TBPARTITION BY MMDD(id3) TBPARTITIONS 3");

        GSI_DEF_INDEX_OPTION.add("");
        GSI_DEF_INDEX_OPTION.add("COMMENT 'gsi test'");

        for (String head : GSI_DEF_HEAD) {
            for (String option : GSI_DEF_INDEX_OPTION) {
                for (String column : GSI_DEF_COLUMN_DB) {
                    buildGsiDef(head, option, column, GSI_DEF_SHARDING_DB);
                }

                for (String column : GSI_DEF_COLUMN_TB) {
                    buildGsiDef(head, option, column, GSI_DEF_SHARDING_TB);
                }
            }
        }
    }

    private static void buildGsiDef(String head, String option, String column, List<String> gsiDefShardingDb) {
        for (String sharding : gsiDefShardingDb) {
            GSI_DEFINITIONS.add("\t" + head + " " + column + " " + sharding + (StringUtils.isBlank(option) ? "" : " ")
                                + option);
        }
    }

    private static void checkExplain(final String sql) {
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) statementList.get(0);

        assertEquals(1, statementList.size());
        String a = StringUtils.replace(sql, " ", "");
        String b = StringUtils.replace(stmt.toString(), " ", "");
        assertEquals(a, b);
    }

    @Test
    public void test() {
        List<Throwable> errors = new ArrayList<Throwable>();
        List<String> errorGsiDefs = new ArrayList<String>();
        for (String gsiDef : GSI_DEFINITIONS) {
            final StringBuilder sqlBuilder = new StringBuilder(CREATE_TABLE_BASE);

            sqlBuilder.append(",\n").append(gsiDef);

            sqlBuilder.append(CREATE_TABLE_TAIL);
            try {
                checkExplain(sqlBuilder.toString());
            } catch (Throwable e) {
                // System.out.println(e.getMessage());
                System.out.println(sqlBuilder.toString());
                e.printStackTrace();
                errors.add(e);
                errorGsiDefs.add(gsiDef);
            }
        }

        if (errors.size() > 0) {
            for (String e : errorGsiDefs) {
                System.out.println(e);
            }
            Assert.fail(errors.size() + " out of " + GSI_DEFINITIONS.size() + " CREATE TABLE statement failed");
        } else {
            System.out.println(GSI_DEFINITIONS.size() + " CREATE TABLE statement success!");
        }
    }

}
