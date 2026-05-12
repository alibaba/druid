package com.alibaba.druid.sql.dialect.hive.parser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveAddJarStatement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HiveStatementParserTest {
    /**
     * 验证add jar类型SQL可以正常解析
     * 例子： add jar hdfs:///hadoop/parser.h.file
     */
    @Test
    public void testAddJarStatement() {
        String s = "add jar hdfs:///hadoop/parser.h.file";
        HiveStatementParser hiveStatementParser = new HiveStatementParser(s);
        SQLStatement sqlStatement = hiveStatementParser.parseAdd();
        assertTrue(sqlStatement instanceof HiveAddJarStatement);
        assertEquals("ADD JAR hdfs:///hadoop/parser.h.file", sqlStatement.toString());
    }
}
