package com.alibaba.druid.bvt.sql.bigquery;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import org.junit.Assert;
import org.junit.Test;

public class BigQueryDataTypeTest {
    @Test
    public void testDataType() {
        String sql = "ANY TYPE";
        SQLExprParser exprParser = SQLParserUtils.createExprParser(sql, DbType.bigquery);
        SQLDataType dataType = exprParser.parseDataType(false);
        Assert.assertEquals(sql, dataType.getName());
    }
}
