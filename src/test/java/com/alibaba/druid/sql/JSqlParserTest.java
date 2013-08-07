package com.alibaba.druid.sql;

import java.io.StringReader;

import junit.framework.TestCase;
import net.sf.jsqlparser.parser.CCJSqlParserManager;

public class JSqlParserTest extends TestCase {
    CCJSqlParserManager parserManager = new CCJSqlParserManager();
    
    public void test_0() throws Exception {
        String statement = "ALTER TABLE `test`.`tb1`  ADD INDEX `ix` (`f2` ASC) ;";
        
        parserManager.parse(new StringReader(statement));
    }
}
