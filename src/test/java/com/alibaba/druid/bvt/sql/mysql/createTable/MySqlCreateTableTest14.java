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
package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import org.junit.Assert;

import java.util.List;

public class MySqlCreateTableTest14 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = " CREATE TABLE `OptionList` ( " + //
                     "`ID` int(11) NOT NULL AUTO_INCREMENT, " + //
                     "`OptionID` int(11) DEFAULT NULL COMMENT '选项ID', " + //
                     "`QuizID` int(11) DEFAULT NULL COMMENT '竞猜题目ID', " + //
                     "`OptionName` varchar(500) DEFAULT NULL COMMENT '选项名称', " + //
                     "`OptionCount` int(11) DEFAULT NULL COMMENT '选择的人数', " + //
                     "PRIMARY KEY (`ID`), KEY `quizId` (`QuizID`) USING BTREE, " + //
                     "KEY `optionId` (`OptionID`) USING BTREE" + //
                     ") ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT=''";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        
        Assert.assertEquals("CREATE TABLE `OptionList` ("
                + "\n\t`ID` int(11) NOT NULL AUTO_INCREMENT,"
                + "\n\t`OptionID` int(11) DEFAULT NULL COMMENT '选项ID',"
                + "\n\t`QuizID` int(11) DEFAULT NULL COMMENT '竞猜题目ID',"
                + "\n\t`OptionName` varchar(500) DEFAULT NULL COMMENT '选项名称',"
                + "\n\t`OptionCount` int(11) DEFAULT NULL COMMENT '选择的人数',"
                + "\n\tPRIMARY KEY (`ID`),"
                + "\n\tKEY `quizId` USING BTREE (`QuizID`),"
                + "\n\tKEY `optionId` USING BTREE (`OptionID`)"
                + "\n) ENGINE = InnoDB CHARSET = gbk COMMENT ''", //
                            SQLUtils.toMySqlString(stmt));
        Assert.assertEquals("create table `OptionList` ("
                + "\n\t`ID` int(11) not null auto_increment,"
                + "\n\t`OptionID` int(11) default null comment '选项ID',"
                + "\n\t`QuizID` int(11) default null comment '竞猜题目ID',"
                + "\n\t`OptionName` varchar(500) default null comment '选项名称',"
                + "\n\t`OptionCount` int(11) default null comment '选择的人数',"
                + "\n\tprimary key (`ID`),"
                + "\n\tkey `quizId` using BTREE (`QuizID`),"
                + "\n\tkey `optionId` using BTREE (`OptionID`)"
                + "\n) engine = InnoDB charset = gbk comment ''", //
                            SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(5, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("OptionList")));

        Assert.assertTrue(visitor.containsColumn("OptionList", "ID"));
        Assert.assertTrue(visitor.containsColumn("OptionList", "OptionCount"));
    }
}
