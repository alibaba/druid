/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql.odps;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class OdpsGrantTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "grant update, Select on table adl_register_baseline_sdt to user DXP_71074213@aliyun.com";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        // System.out.println(output);
        Assert.assertEquals("GRANT UPDATE, SELECT ON TABLE adl_register_baseline_sdt TO USER DXP_71074213@aliyun.com",
                            output);
    }

    public void test_1() throws Exception {
        String sql = "grant role_project_admin to aliyun$DXP_xxxxx@aliyun.com";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        // System.out.println(output);
        Assert.assertEquals("GRANT role_project_admin TO aliyun$DXP_xxxxx@aliyun.com", output);
    }

    public void test_2() throws Exception {
        String sql = "grant super Write to user aliyun$DXP_xxxxx@aliyun.com";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        // System.out.println(output);
        Assert.assertEquals("GRANT SUPER WRITE TO USER aliyun$DXP_xxxxx@aliyun.com", output);
    }

    public void test_3() throws Exception {
        String sql = "grant label 2 on table adl_register_baseline_sdt(c1, c2) to user aliyun$DXP_xxxxx@aliyun.com with exp 5";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        // System.out.println(output);
        Assert.assertEquals("GRANT LABEL 2 ON TABLE adl_register_baseline_sdt(c1, c2) TO USER aliyun$DXP_xxxxx@aliyun.com WITH EXP 5",
                            output);
    }

    public void test_4() throws Exception {
        String sql = "grant CreateInstance, CreateResource, CreateFunction, CreateTable, List ON PROJECT test_project TO ROLE worker";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        // System.out.println(output);
        Assert.assertEquals("GRANT CreateInstance, CreateResource, CreateFunction, CreateTable, LIST ON PROJECT test_project TO ROLE worker",
                            output);
    }
}
