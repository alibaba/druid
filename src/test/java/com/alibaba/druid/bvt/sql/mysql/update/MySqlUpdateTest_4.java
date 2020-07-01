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
package com.alibaba.druid.bvt.sql.mysql.update;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;

public class MySqlUpdateTest_4 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "insert into darenai_stat_url SET user='nologin',ip='58.101.223.183',reffer='http://item.taobao.com/item.htm?spm=a230r.1.14.419.KDVewC&amp;id=17052767689',url='/d/jingpinhui?spm=2013.1.0.0.zr4nLz&amp;ac=shop&amp;imageid=1019937265&amp;s=1259538&amp;s=1259538',shopnick='零利润3232',time=NOW()";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        System.out.println(stmt);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(6, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("darenai_stat_url")));

        Assert.assertTrue(visitor.getColumns().contains(new Column("darenai_stat_url", "user")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("darenai_stat_url", "ip")));

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("INSERT INTO darenai_stat_url (user, ip, reffer, url, shopnick"
                    + "\n\t, time)"
                    + "\nVALUES ('nologin', '58.101.223.183', 'http://item.taobao.com/item.htm?spm=a230r.1.14.419.KDVewC&amp;id=17052767689', '/d/jingpinhui?spm=2013.1.0.0.zr4nLz&amp;ac=shop&amp;imageid=1019937265&amp;s=1259538&amp;s=1259538', '零利润3232'"
                    + "\n\t, NOW())", //
                                output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("insert into darenai_stat_url (user, ip, reffer, url, shopnick"
                    + "\n\t, time)"
                    + "\nvalues ('nologin', '58.101.223.183', 'http://item.taobao.com/item.htm?spm=a230r.1.14.419.KDVewC&amp;id=17052767689', '/d/jingpinhui?spm=2013.1.0.0.zr4nLz&amp;ac=shop&amp;imageid=1019937265&amp;s=1259538&amp;s=1259538', '零利润3232'"
                    + "\n\t, NOW())", //
                                output);
        }
    }
}
