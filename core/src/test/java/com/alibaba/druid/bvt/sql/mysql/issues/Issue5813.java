package com.alibaba.druid.bvt.sql.mysql.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5813>Issue来源</a>
 */
public class Issue5813 {

    @Test
    public void test_parse_create_table() {
        for (DbType dbType : new DbType[]{
            DbType.mysql,
            DbType.mariadb,
        }) {

            for (String sql : new String[]{
                "CREATE TABLE t1 (\n"
                    + "    c1 INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n"
                    + "    c2 TINYINT,\n"
                    + "    c3 SMALLINT,\n"
                    + "    c4 MEDIUMINT,\n"
                    + "    c5 INT,\n"
                    + "    c7 BIGINT,\n"
                    + "    c8 VARCHAR(100) )\n"
                    + "ENGINE=NDB\n"
                    + "COMMENT=\"NDB_TABLE=READ_BACKUP=0,PARTITION_BALANCE=FOR_RP_BY_NODE\";;",
            }) {
                System.out.println(dbType + "原始的sql===" + sql);
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> statementList = parser.parseStatementList();
                String sqlGen = statementList.toString();
                System.out.println(dbType + "生成的sql===" + sqlGen);
                StringBuilder sb = new StringBuilder();
                for (SQLStatement statement : statementList) {
                    sb.append(statement.toString()).append(";");
                }
                sb.deleteCharAt(sb.length() - 1);
                parser = SQLParserUtils.createSQLStatementParser(sb.toString(), dbType);
                List<SQLStatement> statementListNew = parser.parseStatementList();
                String sqlGenNew = statementList.toString();
                System.out.println(dbType + "重新解析再生成的sql===" + sqlGenNew);
                assertEquals(statementList.toString(), statementListNew.toString());
                SQLCreateTableStatement cts = (SQLCreateTableStatement) statementListNew.get(0);
                for (SQLColumnDefinition cd : cts.getColumnDefinitions()) {
                    System.out.println(cd.getNameAsString() + "|" + cd.getDataType() + "|" + cd.getDataType().isInt());
                    if ("MEDIUMINT".equals(cd.getDataType().toString())) {
                        assertTrue(cd.getDataType().isInt());
                    }
                }
            }
        }
    }
}
