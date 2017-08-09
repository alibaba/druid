package com.alibaba.druid.bvt.sql.odps;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class OdpsCreateTableTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS sale_detail(" //
                     + " shop_name     STRING," //
                     + " customer_id   STRING," //
                     + " total_price   DOUBLE" //
                     + ")" //
                     + "comment 'xxxx'" //
                     + "PARTITIONED BY (sale_date STRING,region STRING)" //
                     + "LIFECYCLE 5" //
                     + ";";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        Assert.assertEquals("CREATE TABLE IF NOT EXISTS sale_detail ("//
                + "\n\tshop_name STRING,"//
                + "\n\tcustomer_id STRING,"//
                + "\n\ttotal_price DOUBLE"//
                + "\n)"//
                + "\nCOMMENT 'xxxx'"//
                + "\nPARTITIONED BY ("
                + "\n\tsale_date STRING,"
                + "\n\tregion STRING"
                + "\n)"//
                + "\nLIFECYCLE 5;", output);
    }
}
