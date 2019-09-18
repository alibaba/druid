package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

public class PagerUtilsTest_Limit_mysql_question_placeholder extends TestCase {

    public void  testQuestionLimitPlaceholder1(){
        String sql = "select * from test_table limit ?";
        testQuestionLimitPlaceholderInternal(sql);
    }

    public void  testQuestionLimitPlaceholder2(){
        String sql = "select * from test_table limit 0, ?";
        testQuestionLimitPlaceholderInternal(sql);
    }

    private void testQuestionLimitPlaceholderInternal(String sql){
        List<SQLStatement> statements;
        try{
            statements = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        }catch(ParserException e){
            Assert.fail(e.getMessage());
            return;
        }
        if (statements == null || statements.size() == 0){
            Assert.fail("no sql found!");
            return;
        }
        if (statements.size() != 1) {
            Assert.fail("sql not support count : " + sql);
            return;
        }
        SQLSelectStatement statement = (SQLSelectStatement)statements.get(0);
        if (!(statement instanceof SQLSelectStatement)) {
            Assert.fail("sql not support count : " + sql);
            return;
        }
        SQLSelect select = statement.getSelect();
        PagerUtils.limit(select, JdbcConstants.MYSQL, 0, 200, true);
        SQLUtils.FormatOption options = new SQLUtils.FormatOption();
        options.setPrettyFormat(false);
        options.setUppCase(false);
        assertEquals(sql, SQLUtils.toSQLString(select, JdbcConstants.MYSQL, options));
    }

}
