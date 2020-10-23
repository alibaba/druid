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
package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class HiveCreateTableTest_47_dla
        extends OracleTest {

    public void test_0() throws Exception {
        String sql = "CREATE external TABLE IF NOT EXISTS dwd_tfc_bas_rdnet_inter_info (\n" +
                "inter_id varchar(225) NOT NULL COMMENT ' 路口id ',\n" +
                "inter_name varchar(225) DEFAULT NULL COMMENT ' (当前版本可能有空值)路口名称,按相交道路名命名。规则：等级高的道路放前面；相同等级道路时，路口关联的有名路段放前面。交叉口命名如：长安街与西单北大街路口道路端点命名如：xxx路端点 ',\n" +
                "lng double DEFAULT NULL COMMENT ' 路口中心点经度 ',\n" +
                "lat double DEFAULT NULL COMMENT ' 路口中心点纬度 ',\n" +
                "geohash varchar(225) DEFAULT NULL COMMENT ' geohash 10位 ',\n" +
                "is_signlight bigint DEFAULT NULL COMMENT ' 是否信号灯路口,0：无交通信号灯,1：有交通信号灯 ',\n" +
                "inter_type_no bigint DEFAULT NULL COMMENT ' 1：十字形路口,2：t形路口,3：y形路口,4：环形交叉口,5：匝道、出入口,6：路段（只有两个方向的路口）,7：错位t形路口,8：斜交路口（x型路口）,9：多路路口,10：其他 ',\n" +
                "entrance_cnt bigint DEFAULT NULL COMMENT ' 进口道数量 ',\n" +
                "area_code bigint DEFAULT NULL COMMENT ' 区域编码 ',\n" +
                "area_name varchar(225) DEFAULT NULL COMMENT ' 区域名称 ',\n" +
                "is_corner bigint DEFAULT NULL COMMENT ' 是否综合路口,0不是综合路口，1综合路口，3环岛',\n" +
                "inter_flowtype_no bigint DEFAULT NULL COMMENT '1 交叉口（多进多出）,2 合流口,3 分流口',\n" +
                "data_version varchar(225) NOT NULL COMMENT ' 季度的最后一天，如20180331 ',\n" +
                "adcode varchar(225) NOT NULL COMMENT ' 所属城市代码，标准6位数字代码 ',\n" +
                "PRIMARY KEY (`inter_id`, `data_version`, `adcode`),\n" +
                "KEY `index_id` (`inter_id`)\n" +
                ") ENGINE = innodb CHARSET = utf8 COMMENT '路口基本信息';" ;
        SQLStatement stmt =  SQLUtils.parseSingleStatement(sql, DbType.hive, SQLParserFeature.KeepComments);

        assertEquals("CREATE EXTERNAL TABLE IF NOT EXISTS dwd_tfc_bas_rdnet_inter_info (\n" +
                "\tinter_id varchar(225) NOT NULL COMMENT ' 路口id ',\n" +
                "\tinter_name varchar(225) DEFAULT NULL COMMENT ' (当前版本可能有空值)路口名称,按相交道路名命名。规则：等级高的道路放前面；相同等级道路时，路口关联的有名路段放前面。交叉口命名如：长安街与西单北大街路口道路端点命名如：xxx路端点 ',\n" +
                "\tlng double DEFAULT NULL COMMENT ' 路口中心点经度 ',\n" +
                "\tlat double DEFAULT NULL COMMENT ' 路口中心点纬度 ',\n" +
                "\tgeohash varchar(225) DEFAULT NULL COMMENT ' geohash 10位 ',\n" +
                "\tis_signlight bigint DEFAULT NULL COMMENT ' 是否信号灯路口,0：无交通信号灯,1：有交通信号灯 ',\n" +
                "\tinter_type_no bigint DEFAULT NULL COMMENT ' 1：十字形路口,2：t形路口,3：y形路口,4：环形交叉口,5：匝道、出入口,6：路段（只有两个方向的路口）,7：错位t形路口,8：斜交路口（x型路口）,9：多路路口,10：其他 ',\n" +
                "\tentrance_cnt bigint DEFAULT NULL COMMENT ' 进口道数量 ',\n" +
                "\tarea_code bigint DEFAULT NULL COMMENT ' 区域编码 ',\n" +
                "\tarea_name varchar(225) DEFAULT NULL COMMENT ' 区域名称 ',\n" +
                "\tis_corner bigint DEFAULT NULL COMMENT ' 是否综合路口,0不是综合路口，1综合路口，3环岛',\n" +
                "\tinter_flowtype_no bigint DEFAULT NULL COMMENT '1 交叉口（多进多出）,2 合流口,3 分流口',\n" +
                "\tdata_version varchar(225) NOT NULL COMMENT ' 季度的最后一天，如20180331 ',\n" +
                "\tadcode varchar(225) NOT NULL COMMENT ' 所属城市代码，标准6位数字代码 ',\n" +
                "\tPRIMARY KEY (`inter_id`, `data_version`, `adcode`),\n" +
                "\tKEY `index_id`(`inter_id`)\n" +
                ")\n" +
                "COMMENT '路口基本信息';", stmt.toString());

        assertEquals(stmt.toString(), SQLUtils.parseSingleStatement(stmt.toString(), DbType.hive, SQLParserFeature.KeepComments).toString());
    }

}
