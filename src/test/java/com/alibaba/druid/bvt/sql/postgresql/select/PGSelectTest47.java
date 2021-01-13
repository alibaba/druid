/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class PGSelectTest47 extends PGTest {
    private DbType dbType = JdbcConstants.POSTGRESQL;

    public void test_0() throws Exception {
        String sql = "SELECT\n" +
                "u.userid,\n" +
                "u.phonenumber,\n" +
                "u.cdate,\n" +
                "u.udate,\n" +
                "u.wechatid,\n" +
                "u.wechatnickname,\n" +
                "u.wechattime,\n" +
                "b. ID,\n" +
                "b.username serviceStaffName,\n" +
                "u.appid,\n" +
                "s. ID servicegroupid,\n" +
                "n.total_amount blackNumber\n" +
                "FROM\n" +
                "users u\n" +
                "LEFT JOIN user_servicegroup s ON u.userid = s.userid\n" +
                "LEFT JOIN jcs_user_base b ON s.servicestaffid = b. ID\n" +
                "LEFT JOIN jcs_user_purchase_record r ON u.userid = r.user_id\n" +
                "LEFT JOIN user_black_article_number n ON u.userid = n.userid\n" +
                "WHERE\n" +
                "u.isactive = TRUE\n" +
                "ORDER BY\n" +
                "n.total_amount DESC NULLS LAST";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, dbType);
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals("SELECT u.userid, u.phonenumber, u.cdate, u.udate, u.wechatid\n" +
                "\t, u.wechatnickname, u.wechattime, b.ID, b.username AS serviceStaffName, u.appid\n" +
                "\t, s.ID AS servicegroupid, n.total_amount AS blackNumber\n" +
                "FROM users u\n" +
                "\tLEFT JOIN user_servicegroup s ON u.userid = s.userid\n" +
                "\tLEFT JOIN jcs_user_base b ON s.servicestaffid = b.ID\n" +
                "\tLEFT JOIN jcs_user_purchase_record r ON u.userid = r.user_id\n" +
                "\tLEFT JOIN user_black_article_number n ON u.userid = n.userid\n" +
                "WHERE u.isactive = true\n" +
                "ORDER BY n.total_amount DESC NULLS LAST", SQLUtils.toPGString(stmt));
        
        Assert.assertEquals("select u.userid, u.phonenumber, u.cdate, u.udate, u.wechatid\n" +
                "\t, u.wechatnickname, u.wechattime, b.ID, b.username as serviceStaffName, u.appid\n" +
                "\t, s.ID as servicegroupid, n.total_amount as blackNumber\n" +
                "from users u\n" +
                "\tleft join user_servicegroup s on u.userid = s.userid\n" +
                "\tleft join jcs_user_base b on s.servicestaffid = b.ID\n" +
                "\tleft join jcs_user_purchase_record r on u.userid = r.user_id\n" +
                "\tleft join user_black_article_number n on u.userid = n.userid\n" +
                "where u.isactive = true\n" +
                "order by n.total_amount desc NULLS LAST", SQLUtils.toPGString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        Assert.assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());

        Assert.assertEquals(17, visitor.getColumns().size());
        Assert.assertEquals(5, visitor.getTables().size());
    }
}
