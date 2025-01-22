package com.alibaba.druid.sql.dialect.mysql.testparser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

/**
 * @author 枭博
 * @date 2025/01/21
 */
public class MySqlStatementParserCommonTest {
    @Test
    public void testInsertWithEscape() {
        SQLStatementParser sqlStatementParser
            = new SQLStatementParser("INSERT INTO tb (id, c0) VALUES(1, 'CREATE TABLE tb (\n"
            + "id bigint(20) NOT NULL AUTO_INCREMENT,\n"
            + " c0 varchar(40) NOT NULL COMMENT \\'c0\\',\n"
            + " PRIMARY KEY (id)\n"
            + " )');",
            DbType.mysql);
        SQLStatement sqlStatement = sqlStatementParser.parseInsert();
        // make sure \' in varchar value can parse
        assert sqlStatement instanceof SQLInsertStatement;
    }
}