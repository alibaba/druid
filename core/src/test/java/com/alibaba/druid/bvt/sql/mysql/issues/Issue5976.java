package com.alibaba.druid.bvt.sql.mysql.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5976" >Issue来源</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.4/en/alter-table.html">mysql alter table </a>
 */
public class Issue5976 {


    @Test
    public void test_parse_alter() {
        for (DbType dbType : new DbType[]{DbType.mysql}) {
            for (String sql : new String[]{
                "ALTER TABLE tbl_name\n"
                    + "    ALTER CHECK symbol  ENFORCED;",

//                "ALTER TABLE t1 \n"
//                    + "  CHANGE COLUMN c1 c1 BLOB \n"
//                    + "    COMMENT = 'NDB_COLUMN=BLOB_INLINE_SIZE=4096,MAX_BLOB_PART_SIZE';",

                "ALTER TABLE tbl_name\n"
                    + "    DROP CHECK symbol;",
                "alter table test.abc comment 'abc' engine MyISAM, rename abc_test",
                "ALTER TABLE t2 DROP COLUMN c, DROP COLUMN d;",
                "ALTER TABLE t1 ENGINE = InnoDB;",
                "ALTER TABLE t1 ROW_FORMAT = COMPRESSED;",
                "ALTER TABLE t1 AUTO_INCREMENT = 13;",
                "ALTER TABLE t1 CHARACTER SET = utf8mb4;",
                "ALTER TABLE t1 COMMENT = 'New table comment';",
                "ALTER TABLE t1 COMMENT = \"NDB_TABLE=READ_BACKUP=0,PARTITION_BALANCE=FOR_RA_BY_NODE\";",
                "ALTER TABLE t1 CHANGE a b BIGINT NOT NULL;",
                "ALTER TABLE t1 CHANGE b b INT NOT NULL;",
                "ALTER TABLE t1 CHANGE b a INT NOT NULL;",
                "-- swap a and b\n"
                    + "ALTER TABLE t1 RENAME COLUMN a TO b,\n"
                    + "               RENAME COLUMN b TO a;\n"
                    + "-- \"rotate\" a, b, c through a cycle\n"
                    + "ALTER TABLE t1 RENAME COLUMN a TO b,\n"
                    + "               RENAME COLUMN b TO c,\n"
                    + "               RENAME COLUMN c TO a;",
                "ALTER TABLE t1 MODIFY col1 BIGINT;",
                "ALTER TABLE t1 MODIFY col1 BIGINT UNSIGNED DEFAULT 1 COMMENT 'my column';",
                "ALTER TABLE tbl_name DROP FOREIGN KEY fk_symbol;",
                "ALTER TABLE tbl_name\n"
                    + "    ALTER CHECK symbol NOT ENFORCED;",
                "ALTER TABLE tbl_name\n"
                    + "    DROP CONSTRAINT symbol;",
                "ALTER TABLE tbl_name\n"
                    + "    ALTER CONSTRAINT symbol  ENFORCED;",
                "ALTER TABLE tbl_name\n"
                    + "    ALTER CONSTRAINT symbol NOT ENFORCED;",
                "ALTER TABLE tbl_name CONVERT TO CHARACTER SET charset_name;",
                "ALTER TABLE t MODIFY latin1_text_col TEXT CHARACTER SET utf8mb4;\n"
                    + "ALTER TABLE t MODIFY latin1_varchar_col VARCHAR(M) CHARACTER SET utf8mb4;",
                "ALTER TABLE t1 CHANGE c1 c1 BLOB;\n"
                    + "ALTER TABLE t1 CHANGE c1 c1 TEXT CHARACTER SET utf8mb4;",
                "ALTER TABLE tbl_name DEFAULT CHARACTER SET charset_name;",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> statementList = parser.parseStatementList();
                System.out.println(statementList);
                //assertEquals(1, statementList.size());
                SQLParseAssertUtil.assertParseSql(sql, dbType);
            }
        }
    }


    @Test
    public void test_parse_rename() {
        for (DbType dbType : new DbType[]{DbType.mysql}) {
            for (String sql : new String[]{
                "alter table test.abc comment 'abc' engine MyISAM, rename abc_test",
                "alter table test.abc comment 'abc' "
                    + "engine MyISAM, rename abc_test RENAME COLUMN old_col_name TO new_col_name",
                "RENAME TABLE old_table TO new_table;",
                "ALTER TABLE old_table RENAME new_table;",
                "RENAME TABLE old_table1 TO new_table1,\n"
                    + "             old_table2 TO new_table2,\n"
                    + "             old_table3 TO new_table3;",
                "RENAME TABLE old_table TO tmp_table,\n"
                    + "             new_table TO old_table,\n"
                    + "             tmp_table TO new_table;",
                "LOCK TABLE old_table1 WRITE;\n"
                    + "RENAME TABLE old_table1 TO new_table1,\n"
                    + "             new_table1 TO new_table2;",
                "LOCK TABLE old_table1 READ;\n"
                    + "RENAME TABLE old_table1 TO new_table1,\n"
                    + "             new_table1 TO new_table2;",
                "RENAME TABLE current_db.tbl_name TO other_db.tbl_name;",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> statementList = parser.parseStatementList();
                System.out.println(statementList);
                //assertEquals(1, statementList.size());
                SQLParseAssertUtil.assertParseSql(sql, dbType);
            }
        }
    }
}
