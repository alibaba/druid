package com.alibaba.druid.bvt.sql.starrocks.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateResourceStatement;
import com.alibaba.druid.sql.dialect.starrocks.visitor.StarRocksOutputVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StarRocksOutputVisitorTest {
    @Test
    public void testStarRocksOutputVisitor() {
        String message = "output error";

        String origin =
                "CREATE TABLE IF NOT EXISTS `detailDemo` (\n" +
                        "\t`recruit_date` DATE NOT NULL COMMENT 'YYYY-MM-DD',\n" +
                        "\t`region_num` TINYINT COMMENT 'range [-128, 127]'\n" +
                        ") ENGINE = OLAP\n" +
                        "DUPLICATE KEY (`recruit_date`, `region_num`)\n" +
                        "COMMENT 'xxxxx'\n" +
                        "PARTITION BY RANGE(`recruit_date`)\n" +
                        "(\n" +
                        "  PARTITION p202101 VALUES [(\"20210101\"),(\"20210201\")),\n" +
                        "  PARTITION p202102 VALUES [(\"20210201\"),(\"20210301\"))\n" +
                        ")\n" +
                        "DISTRIBUTED BY HASH(`recruit_date`, `region_num`) BUCKETS 8\n" +
                        "PROPERTIES (\n" +
                        "\t\"storage_medium\" = \"SSD\",\n" +
                        "\t\"replication_num\" = \"3\"\n" +
                        ")";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(origin, DbType.starrocks);
        StringBuilder builder = new StringBuilder();
        StarRocksOutputVisitor visitor = new StarRocksOutputVisitor(builder);
        visitor.setUppCase(false);
        visitor.setPrettyFormat(false);
        visitor.setParameterized(false);
        SQLStatement stmt = parser.parseStatement();
        stmt.accept(visitor);
        String result = builder.toString();
        assertNotNull(result);
        assertTrue(result.contains("detailDemo"));
        assertTrue(result.toLowerCase().contains("distributed by hash"));
        assertTrue(result.toLowerCase().contains("properties"));
    }

    @Test
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
