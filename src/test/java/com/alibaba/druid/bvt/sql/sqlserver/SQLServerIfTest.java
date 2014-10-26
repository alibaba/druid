/*
 * Copyright 2014 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.druid.bvt.sql.sqlserver;

import org.junit.Assert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

import junit.framework.TestCase;

public class SQLServerIfTest extends TestCase {

    public void test() {
        String sql = "if @is_exists is null begin  insert into Inventory_1 (co_id,sku_id,order_lock) values (@0,@1,@2) end else begin insert into Inventory_2 (co_id,sku_id,order_lock) values (@0,@1,@2) end ";
        String expect = "IF @is_exists IS NULL"//
                        + "\n\tBEGIN"//
                        + "\n\t\tINSERT INTO Inventory_1"//
                        + "\n\t\t\t(co_id, sku_id, order_lock)"//
                        + "\n\t\tVALUES"//
                        + "\n\t\t(@0, @1, @2);"//
                        + "\n\tEND"//
                        + "\nELSE"//
                        + "\n\tBEGIN"//
                        + "\n\t\tINSERT INTO Inventory_2"//
                        + "\n\t\t\t(co_id, sku_id, order_lock)"//
                        + "\n\t\tVALUES"//
                        + "\n\t\t(@0, @1, @2);"//
                        + "\n\tEND";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);

        String text = TestUtils.outputSqlServer(stmt);

        Assert.assertEquals(expect, text);

        System.out.println(text);
    }

}
