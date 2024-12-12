package com.alibaba.druid.bvt.sql.starrocks;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StarrocksKeywordsTest {
    @Test
    public void test_keywords(){
        DbType dbType = DbType.starrocks;
        String sql =
            "CREATE TABLE test (\n"
                + "\tkey int(11) NOT NULL COMMENT 'key',\n"
                + "\tgroup varchar(255) NULL COMMENT '分组',\n"
                + "\tevent_scen varchar(255) NULL COMMENT '事件触发场景',\n"
                + "\tenable varchar(2) NULL COMMENT '是否生效 0 失效 1 生效',\n"
                + "\tcreate_time datetime NULL COMMENT '创建时间',\n"
                + "\tupdate_by varchar(255) NULL COMMENT '更新人',\n"
                + "\tupdate_time datetime NULL COMMENT '更新时间',\n"
                + "\tcreate_by varchar(255) NULL COMMENT '创建人',\n"
                + "\tvalues varchar(50) NULL COMMENT 'values'\n"
                + ") ENGINE = OLAP\n"
                + "UNIQUE KEY (id)\n"
                + "COMMENT \"埋点业务表\"\n"
                + "DISTRIBUTED BY HASH (id) BUCKETS 8\n"
                + "PROPERTIES (\n"
                + "\t\"replication_num\" = \"tag.location.default: 3\",\n"
                + "\t\"storage_format\" = \"V2\"\n"
                + ");";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, 
                dbType,
                SQLParserFeature.IgnoreNameQuotes);
        SQLStatement stmt = parser.parseStatement();
        assertEquals(Token.EOF, parser.getLexer().token());
        String result = SQLUtils.toSQLString(stmt, dbType, null, VisitorFeature.OutputNameQuote).trim();
        String expectedSql = "CREATE TABLE test (\n"
                + "\t`key` int(11) NOT NULL COMMENT 'key',\n"
                + "\t`group` varchar(255) NULL COMMENT '分组',\n"
                + "\tevent_scen varchar(255) NULL COMMENT '事件触发场景',\n"
                + "\tenable varchar(2) NULL COMMENT '是否生效 0 失效 1 生效',\n"
                + "\tcreate_time datetime NULL COMMENT '创建时间',\n"
                + "\tupdate_by varchar(255) NULL COMMENT '更新人',\n"
                + "\tupdate_time datetime NULL COMMENT '更新时间',\n"
                + "\tcreate_by varchar(255) NULL COMMENT '创建人',\n"
                + "\t`values` varchar(50) NULL COMMENT 'values'\n"
                + ") ENGINE = OLAP\n"
                + "UNIQUE KEY (id)\n"
                + "COMMENT \"埋点业务表\"\n"
                + "DISTRIBUTED BY HASH (id) BUCKETS 8\n"
                + "PROPERTIES (\n"
                + "\t\"replication_num\" = \"tag.location.default: 3\",\n"
                + "\t\"storage_format\" = \"V2\"\n"
                + ");";
        Assert.assertEquals(expectedSql, result);
    }
}
