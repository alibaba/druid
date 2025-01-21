package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.repository.SchemaObject;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.util.JdbcConstants;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class MySqlCreateTableStatementTest {
    @Test
    public void testAlterTable() {
        SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);
        repository.setDefaultSchema("`db`");

        // init table struct
        repository.console("CREATE TABLE tb (\n"
            + "\tid bigint(20) NOT NULL AUTO_INCREMENT,\n"
            + "\tc0 varchar(40) NOT NULL COMMENT 'c0',\n"
            + "\tPRIMARY KEY (id)\n"
            + ")");
        // expect 2 column
        assertArrayEquals(new String[] {"id", "c0"}, findColumnNames(repository.findSchema("db").findTable("tb")));

        // add column with alter option
        repository.console("ALTER TABLE `db`.`tb` ADD COLUMN `c1` int COMMENT 'c1' , algorithm=inplace ,lock=none");
        SchemaObject schemaObject = repository.findSchema("db").findTable("tb");
        // expect 3 column after add column
        assertArrayEquals(new String[] {"id", "c0", "c1"}, findColumnNames(schemaObject));

        // generate create table sql
        String createTableSql = schemaObject.getStatement().toString();

        // restore from full create table sql
        repository = new SchemaRepository(JdbcConstants.MYSQL);
        repository.setDefaultSchema("`db`");
        repository.console(createTableSql);

        // expect 3 column after restore
        assertArrayEquals(new String[] {"id", "c0", "c1"},
            findColumnNames(repository.findSchema("db").findTable("tb")));
    }

    private Object[] findColumnNames(SchemaObject schemaObject) {
        SQLStatement statement = schemaObject.getStatement();
        assert statement instanceof MySqlCreateTableStatement;
        return ((MySqlCreateTableStatement)statement).getTableElementList()
            .stream()
            .filter(element -> element instanceof SQLColumnDefinition)
            .map(element -> ((SQLColumnDefinition)element).getName())
            .map(SQLName::getSimpleName)
            .toArray();
    }
}