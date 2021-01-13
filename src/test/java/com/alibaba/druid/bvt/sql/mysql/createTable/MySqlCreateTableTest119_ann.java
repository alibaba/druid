package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlTableIndex;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlCreateTableTest119_ann extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE face_feature (\n" +
                "  id varchar COMMENT 'id',\n" +
                "  facefea array<short>(256) COMMENT 'feature',\n" +
                "  ANN INDEX facefea_index1 (facefea) DistanceMeasure = DotProduct ALGORITHM = IVF\n" +
                "  PRIMARY KEY (id)\n" +
                ")\n" +
                "PARTITION BY HASH KEY (id) PARTITION NUM 8\n" +
                "TABLEGROUP vector_demo_group\n" +
                "OPTIONS (UPDATETYPE='batch')\n" +
                "COMMENT '';";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ALIYUN_DRDS);
        SQLCreateTableStatement stmt = (SQLCreateTableStatement) statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(4, stmt.getTableElementList().size());

        assertEquals("CREATE TABLE face_feature (\n" +
                "\tid varchar COMMENT 'id',\n" +
                "\tfacefea ARRAY<short>(256) COMMENT 'feature',\n" +
                "\tINDEX facefea_index1 ANN(facefea) ALGORITHM = IVF DistanceMeasure = DotProduct,\n" +
                "\tPRIMARY KEY (id)\n" +
                ")\n" +
                "OPTIONS (UPDATETYPE = 'batch') COMMENT ''\n" +
                "PARTITION BY HASH KEY(id) PARTITION NUM 8\n" +
                "TABLEGROUP vector_demo_group;", stmt.toString());

        MySqlTableIndex idx = (MySqlTableIndex) stmt.findIndex("facefea");
        assertNotNull(idx);

        assertEquals("DotProduct", idx.getDistanceMeasure());
        assertEquals("IVF", idx.getAlgorithm());
        assertEquals("ANN", idx.getIndexType());
    }
}