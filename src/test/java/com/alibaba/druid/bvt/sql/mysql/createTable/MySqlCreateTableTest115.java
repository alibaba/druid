package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.sql.Types;
import java.util.List;

public class MySqlCreateTableTest115 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS `schema`.`Employee` (\n" +
                "   id int(10),\n" +
                "   `Employee_id` VARCHAR(45) NOT NULL ,\n" +
                "   `Name` VARCHAR(255) NULL ,\n" +
                "   `idAddresses` VARCHAR(45) NULL ,\n" +
                "   PRIMARY KEY (`Employee_id`) ,\n" +
                "   INDEX `employee_id` USING BTREE (id(5)),\n" +
                "   CONSTRAINT `fkEmployee_Addresses`\n" +
                "   FOREIGN KEY `fkEmployee_Addresses` (`idAddresses`)\n" +
                "   REFERENCES `schema`.`Addresses` (`idAddresses`)\n" +
                "   ON DELETE NO ACTION\n" +
                "   ON UPDATE NO ACTION\n" +
                " ) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_bin";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(7, stmt.getTableElementList().size());

        assertEquals("CREATE TABLE IF NOT EXISTS `schema`.`Employee` (\n" +
                "\tid int(10),\n" +
                "\t`Employee_id` VARCHAR(45) NOT NULL,\n" +
                "\t`Name` VARCHAR(255) NULL,\n" +
                "\t`idAddresses` VARCHAR(45) NULL,\n" +
                "\tPRIMARY KEY (`Employee_id`),\n" +
                "\tINDEX `employee_id` USING BTREE(id(5)),\n" +
                "\tCONSTRAINT `fkEmployee_Addresses` FOREIGN KEY `fkEmployee_Addresses` (`idAddresses`) REFERENCES `schema`.`Addresses` (`idAddresses`) ON DELETE NO ACTION ON UPDATE NO ACTION\n" +
                ") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin", stmt.toString());

        SchemaStatVisitor v = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(v);

        assertEquals(5, v.getColumns().size());
        SQLColumnDefinition column = stmt.findColumn("Employee_id");
        assertNotNull(column);
        assertEquals(1, column.getConstraints().size());
        assertTrue(column.isPrimaryKey());
        assertEquals(Types.VARCHAR, column.jdbcType());

        assertEquals("CREATE TABLE IF NOT EXISTS `schema`.`Employee` (\n" +
                "\tid int(10),\n" +
                "\t`Employee_id` VARCHAR(45) NOT NULL,\n" +
                "\t`Name` VARCHAR(255) NULL,\n" +
                "\t`idAddresses` VARCHAR(45) NULL,\n" +
                "\tPRIMARY KEY (`Employee_id`),\n" +
                "\tINDEX `employee_id` USING BTREE(id(5)),\n" +
                "\tFOREIGN KEY `fkEmployee_Addresses` (`idAddresses`) REFERENCES `schema`.`Addresses` (`idAddresses`) ON DELETE NO ACTION ON UPDATE NO ACTION\n" +
                ") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin", stmt.clone().toString());

        SchemaRepository repository = new SchemaRepository(DbType.mysql);
        repository.acceptDDL(sql);
        assertEquals("CREATE TABLE IF NOT EXISTS `Employee` (\n" +
                "\tid int(10),\n" +
                "\t`Employee_id` VARCHAR(45) NOT NULL,\n" +
                "\t`Name` VARCHAR(255) NULL,\n" +
                "\t`idAddresses` VARCHAR(45) NULL,\n" +
                "\tPRIMARY KEY (`Employee_id`),\n" +
                "\tINDEX `employee_id` USING BTREE(id(5)),\n" +
                "\tFOREIGN KEY `fkEmployee_Addresses` (`idAddresses`) REFERENCES `schema`.`Addresses` (`idAddresses`) ON DELETE NO ACTION ON UPDATE NO ACTION\n" +
                ") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin", repository
                .findTable("Employee")
                .getStatement()
                .toString());
    }

}