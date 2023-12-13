package com.alibaba.druid.sql.dialect.starrocks.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateResourceStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import junit.framework.TestCase;
import org.junit.Assert;

public class StarRocksOutputVisitorTest extends TestCase {

    public void testStarRocksOutputVisitor(){

        String message = "output error";

        String origin =
            "CREATE TABLE IF NOT EXISTS `detailDemo` (\n" +
                "\t`recruit_date` DATE NOT NULL COMMENT 'YYYY-MM-DD',\n" +
                "\t`region_num` TINYINT COMMENT 'range [-128, 127]',\n" +
                "\t`num_plate` SMALLINT COMMENT 'range [-32768, 32767] ',\n" +
                "\t`tel` INT COMMENT 'range [-2147483648, 2147483647]',\n" +
                "\t`id` BIGINT COMMENT 'range [-2^63 + 1 ~ 2^63 - 1]',\n" +
                "\t`password` LARGEINT COMMENT 'range [-2^127 + 1 ~ 2^127 - 1]',\n" +
                "\t`name` CHAR(20) NOT NULL COMMENT 'range char(m),m in (1-255)',\n" +
                "\t`profile` VARCHAR(500) NOT NULL COMMENT 'upper limit value 1048576 bytes',\n" +
                "\t`hobby` STRING NOT NULL COMMENT 'upper limit value 65533 bytes',\n" +
                "\t`leave_time` DATETIME COMMENT 'YYYY-MM-DD HH:MM:SS',\n" +
                "\t`channel` FLOAT COMMENT '4 bytes',\n" +
                "\t`income` DOUBLE COMMENT '8 bytes',\n" +
                "\t`account` DECIMAL(12, 4) COMMENT '\"\"',\n" +
                "\t`ispass` BOOLEAN COMMENT 'true/false'\n" +
                ") ENGINE = OLAP\n" +
                "DUPLICATE KEY(`recruit_date`, `region_num`)\n" +
                "COMMENT 'xxxxx'\n" +
                "PARTITION BY RANGE(`recruit_date`)\n" +
                "(\n" +
                "  PARTITION p202101 VALUES [(\"20210101\"),(\"20210201\")),\n" +
                "  PARTITION p202102 VALUES [(\"20210201\"),(\"20210301\")),\n" +
                "  PARTITION p202103 VALUES [(\"20210301\"),(MAXVALUE))\n" +
                ")\n" +
                "DISTRIBUTED BY HASH(`recruit_date`, `region_num`) BUCKETS 8\n" +
                "PROPERTIES (\n" +
                "  \"storage_medium\" = \"[SSD|HDD]\",\n" +
                "  \"dynamic_partition.enable\" = \"true|false\",\n" +
                "  \"dynamic_partition.time_unit\" = \"DAY|WEEK|MONTH\",\n" +
                "  \"dynamic_partition.start\" = \"${integer_value}\",\n" +
                "  [\"storage_cooldown_time\" = \"yyyy-MM-dd HH:mm:ss\",]\n" +
                "  [\"replication_num\" = \"3\"]\n" +
                ")";

        String expected = "CREATE TABLE IF NOT EXISTS `detailDemo` ( `recruit_date` DATE NOT NULL COMMENT 'YYYY-MM-DD', `region_num` TINYINT COMMENT 'range [-128, 127]', `num_plate` SMALLINT COMMENT 'range [-32768, 32767] ', `tel` INT COMMENT 'range [-2147483648, 2147483647]', `id` BIGINT COMMENT 'range [-2^63 + 1 ~ 2^63 - 1]', `password` LARGEINT COMMENT 'range [-2^127 + 1 ~ 2^127 - 1]', `name` CHAR(20) NOT NULL COMMENT 'range char(m),m in (1-255)', `profile` VARCHAR(500) NOT NULL COMMENT 'upper limit value 1048576 bytes', `hobby` STRING NOT NULL COMMENT 'upper limit value 65533 bytes', `leave_time` DATETIME COMMENT 'YYYY-MM-DD HH:MM:SS', `channel` FLOAT COMMENT '4 bytes', `income` DOUBLE COMMENT '8 bytes', `account` DECIMAL(12, 4) COMMENT '\"\"', `ispass` BOOLEAN COMMENT 'true/false' ) ENGINE = OLAP DUPLICATE KEY (`recruit_date`, `region_num`) COMMENT 'xxxxx' PARTITION BY RANGE(`recruit_date`) (   PARTITION p202101 VALUES [(\"20210101\"),(\"20210201\")),   PARTITION p202102 VALUES [(\"20210201\"),(\"20210301\")),   PARTITION p202103 VALUES [(\"20210301\"),(MAXVALUE)) ) DISTRIBUTED BY HASH(`recruit_date`, `region_num`) BUCKETS 8 PROPERTIES ( \"storage_medium\" = \"[SSD|HDD]\", \"dynamic_partition.enable\" = \"true|false\", \"dynamic_partition.time_unit\" = \"DAY|WEEK|MONTH\", \"dynamic_partition.start\" = \"${integer_value}\", [\"storage_cooldown_time\" = \"yyyy-MM-dd HH:mm:ss\",] [\"replication_num\" = \"3\"] )";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(origin, DbType.starrocks);
        StringBuilder builder = new StringBuilder();
        StarRocksOutputVisitor visitor = new StarRocksOutputVisitor(builder);
        visitor.setUppCase(false);
        visitor.setPrettyFormat(false);
        visitor.setParameterized(false);
        SQLStatement stmt = parser.parseStatement();
        stmt.accept(visitor);
        String result = builder.toString();
        Assert.assertEquals(message, expected, result);
    }

    public void testCreateResourceStmt() {
        StarRocksCreateResourceStatement stmt = new StarRocksCreateResourceStatement();
        stmt.setExternal(true);
        stmt.setName(new SQLIdentifierExpr("spark"));
        stmt.addProperty(new SQLCharExpr("spark.master"), new SQLCharExpr("yarn"));
        String expected = "CREATE EXTERNAL RESOURCE spark\n" +
                "PROPERTIES (\n" +
                "\t'spark.master' = 'yarn'\n" +
                ")";
        assertEquals(expected, stmt.toString());
    }
}
