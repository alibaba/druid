package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest127_ads_ann extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE adl_new_retail.adl_sec_verify_face ( \n" +
                "   id varchar NOT NULL COMMENT '', \n" +
                "   xid varchar NOT NULL COMMENT ''\n, " +
                "   card_no varchar COMMENT '', \n" +
                "   face_oss varchar COMMENT '', idpc float COMMENT '', fidpc float COMMENT '', face_feature float[256] NOT NULL COMMENT '', \n" +
                "gender smallint COMMENT '', age smallint COMMENT '', \n" +
                "ANN INDEX feature_idx0 (face_feature) DistanceMeasure=DotProduct Algorithm=FAST_INDEX, PRIMARY KEY (id,xid) \n" +
                ") PARTITION BY HASH (ComSubStr (xid, face_oss)) PARTITION NUM 16 TABLEGROUP deepvision OPTIONS (UPDATETYPE='realtime') COMMENT 'verify photos';";
//        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE adl_new_retail.adl_sec_verify_face (\n" +
                "\tid varchar NOT NULL COMMENT '',\n" +
                "\txid varchar NOT NULL COMMENT '',\n" +
                "\tcard_no varchar COMMENT '',\n" +
                "\tface_oss varchar COMMENT '',\n" +
                "\tidpc float COMMENT '',\n" +
                "\tfidpc float COMMENT '',\n" +
                "\tface_feature float[256] NOT NULL COMMENT '',\n" +
                "\tgender smallint COMMENT '',\n" +
                "\tage smallint COMMENT '',\n" +
                "\tINDEX feature_idx0 ANN(face_feature) ALGORITHM = FAST_INDEX DistanceMeasure = DotProduct,\n" +
                "\tPRIMARY KEY (id, xid)\n" +
                ")\n" +
                "OPTIONS (UPDATETYPE = 'realtime') COMMENT 'verify photos'\n" +
                "PARTITION BY HASH (ComSubStr(xid, face_oss)) PARTITION NUM 16\n" +
                "TABLEGROUP deepvision;", stmt.toString());

    }



}