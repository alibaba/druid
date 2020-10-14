package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.sql.Types;
import java.util.List;

public class MySqlCreateTableTest121_fulltext extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS ddd ( pk int NOT NULL PRIMARY KEY AUTO_INCREMENT" +
                ", tint tinyint(10) UNSIGNED ZEROFILL, sint smallint DEFAULT 1000, mint mediumint UNIQUE, bint bigint(20) COMMENT 'bigint'" +
                ", rint real(10, 2) REFERENCES tt1 (rint) MATCH FULL ON DELETE RESTRICT, dble double(10, 2) REFERENCES tt1 (dble) MATCH PARTIAL ON DELETE CASCADE" +
                ", fl float(10, 2) REFERENCES tt1 (fl) MATCH SIMPLE ON DELETE SET NULL, dc decimal(10, 2) REFERENCES tt1 (dc) MATCH SIMPLE ON UPDATE NO ACTION" +
                ", num numeric(10, 2), dt date NULL, ti time, tis timestamp, dti datetime" +
                ", vc varchar(100) BINARY , vc2 varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL, tb tinyblob, bl blob, mb mediumblob" +
                ", lb longblob, tt tinytext, mt mediumtext, lt longtext, en enum('1', '2'), st set('5', '6'), id1 int, id2 int, id3 varchar(100)" +
                ", vc1 varchar(100), vc3 varchar(100), INDEX idx1 USING hash(id1), KEY idx2 USING hash (id2), FULLTEXT key idx4(id3(20))" +
                ", CONSTRAINT c1 UNIQUE c1 USING btree (vc1(20)) ) ENGINE = InnoDB AUTO_INCREMENT = 2 AVG_ROW_LENGTH = 100 CHARACTER SET = utf8 COLLATE utf8_bin CHECKSUM = 0 COMMENT 'abcd'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(34, stmt.getTableElementList().size());

        assertEquals("CREATE TABLE IF NOT EXISTS ddd (\n" +
                "\tpk int NOT NULL PRIMARY KEY AUTO_INCREMENT,\n" +
                "\ttint tinyint(10) UNSIGNED ZEROFILL,\n" +
                "\tsint smallint DEFAULT 1000,\n" +
                "\tmint mediumint UNIQUE,\n" +
                "\tbint bigint(20) COMMENT 'bigint',\n" +
                "\trint real(10, 2) REFERENCES tt1 (rint) MATCH FULL ON DELETE RESTRICT,\n" +
                "\tdble double(10, 2) REFERENCES tt1 (dble) MATCH PARTIAL ON DELETE CASCADE,\n" +
                "\tfl float(10, 2) REFERENCES tt1 (fl) MATCH SIMPLE ON DELETE SET NULL,\n" +
                "\tdc decimal(10, 2) REFERENCES tt1 (dc) MATCH SIMPLE ON UPDATE NO ACTION,\n" +
                "\tnum numeric(10, 2),\n" +
                "\tdt date NULL,\n" +
                "\tti time,\n" +
                "\ttis timestamp,\n" +
                "\tdti datetime,\n" +
                "\tvc varchar(100) BINARY ,\n" +
                "\tvc2 varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,\n" +
                "\ttb tinyblob,\n" +
                "\tbl blob,\n" +
                "\tmb mediumblob,\n" +
                "\tlb longblob,\n" +
                "\ttt tinytext,\n" +
                "\tmt mediumtext,\n" +
                "\tlt longtext,\n" +
                "\ten enum('1', '2'),\n" +
                "\tst set('5', '6'),\n" +
                "\tid1 int,\n" +
                "\tid2 int,\n" +
                "\tid3 varchar(100),\n" +
                "\tvc1 varchar(100),\n" +
                "\tvc3 varchar(100),\n" +
                "\tINDEX idx1 USING hash(id1),\n" +
                "\tKEY idx2 USING hash (id2),\n" +
                "\tFULLTEXT KEY idx4 (id3(20)),\n" +
                "\tUNIQUE c1 USING btree (vc1(20))\n" +
                ") ENGINE = InnoDB AUTO_INCREMENT = 2 AVG_ROW_LENGTH = 100 CHARACTER SET = utf8 COLLATE = utf8_bin CHECKSUM = 0 COMMENT 'abcd'", stmt.toString());

        assertEquals("create table if not exists ddd (\n" +
                "\tpk int not null primary key auto_increment,\n" +
                "\ttint tinyint(10) unsigned zerofill,\n" +
                "\tsint smallint default 1000,\n" +
                "\tmint mediumint unique,\n" +
                "\tbint bigint(20) comment 'bigint',\n" +
                "\trint real(10, 2) references tt1 (rint) match full on delete restrict,\n" +
                "\tdble double(10, 2) references tt1 (dble) match partial on delete cascade,\n" +
                "\tfl float(10, 2) references tt1 (fl) match simple on delete set null,\n" +
                "\tdc decimal(10, 2) references tt1 (dc) match simple on update no action,\n" +
                "\tnum numeric(10, 2),\n" +
                "\tdt date null,\n" +
                "\tti time,\n" +
                "\ttis timestamp,\n" +
                "\tdti datetime,\n" +
                "\tvc varchar(100) binary ,\n" +
                "\tvc2 varchar(100) character set utf8 collate utf8_bin not null,\n" +
                "\ttb tinyblob,\n" +
                "\tbl blob,\n" +
                "\tmb mediumblob,\n" +
                "\tlb longblob,\n" +
                "\ttt tinytext,\n" +
                "\tmt mediumtext,\n" +
                "\tlt longtext,\n" +
                "\ten enum('1', '2'),\n" +
                "\tst set('5', '6'),\n" +
                "\tid1 int,\n" +
                "\tid2 int,\n" +
                "\tid3 varchar(100),\n" +
                "\tvc1 varchar(100),\n" +
                "\tvc3 varchar(100),\n" +
                "\tindex idx1 using hash(id1),\n" +
                "\tkey idx2 using hash (id2),\n" +
                "\tfulltext key idx4 (id3(20)),\n" +
                "\tunique c1 using btree (vc1(20))\n" +
                ") engine = InnoDB auto_increment = 2 avg_row_length = 100 character set = utf8 collate = utf8_bin checksum = 0 comment 'abcd'", stmt.toLowerCaseString());

        SchemaStatVisitor v = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(v);

        assertEquals(30, v.getColumns().size());
        SQLColumnDefinition column = stmt.findColumn("bint");
        assertNotNull(column);
        assertEquals(0, column.getConstraints().size());
        assertFalse(column.isPrimaryKey());
        assertEquals(Types.BIGINT, column.jdbcType());
    }

}