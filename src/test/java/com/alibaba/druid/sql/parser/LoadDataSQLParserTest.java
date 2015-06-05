package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLoadDataInFileStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * Created by magicdoom on 2015/6/5.
 */
public class LoadDataSQLParserTest   extends TestCase
{
    public void test_load_data() throws Exception {
        String sql = "load DATA  local INFILE '/opt/test.txt' IGNORE INTO TABLE test  CHARACTER SET 'utf8' FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\"'  ESCAPED BY  '\\\\'   LINES TERMINATED BY '\r\n' (id,sid,asf) ";
        SQLStatementParser parser = new MySqlStatementParser(sql);
        MySqlLoadDataInFileStatement stmt = (MySqlLoadDataInFileStatement) parser.parseStatement(); //
        Assert.assertEquals("utf8",stmt.getCharset()) ;
        Assert.assertEquals(",",((SQLCharExpr)stmt.getColumnsTerminatedBy()).getText()) ;
        Assert.assertEquals("\"",((SQLCharExpr)stmt.getColumnsEnclosedBy()).getText()) ;
        Assert.assertEquals("\r\n",((SQLCharExpr)stmt.getLinesTerminatedBy()).getText()) ;
        Assert.assertEquals("\\",((SQLCharExpr)stmt.getColumnsEscaped()).getText()) ;

    }
}
