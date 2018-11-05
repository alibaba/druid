package com.alibaba.druid.mysql;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import junit.framework.TestCase;

public class DeleteStackOverflowTest extends TestCase {
    public void testOverflow(){
        String sql = "delete c, cm FROM `wp_88comments` c LEFT JOIN `wp_88commentmeta` cm ON c.comment_ID = cm.comment_id WHERE comment_approved = '0'";
        SQLStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statement.accept(visitor);
    }
}

