package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;

import java.util.ArrayList;
import java.util.List;

public class MySqlSelectTest_trim extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select trim('x' from 'xxdxx')";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, SQLParserFeature.TDDLHint);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        //assertEquals("SELECT trim('x')", stmt.toString());


        List<Object> outParameters = new ArrayList<Object>();
        String parameterizeSql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, outParameters,
                VisitorFeature.OutputParameterizedQuesUnMergeInList,
                VisitorFeature.OutputParameterizedUnMergeShardingTable,
                VisitorFeature.OutputParameterizedQuesUnMergeValuesList);

        System.out.println(parameterizeSql);
        System.out.println(outParameters);

        sql = "select trim('x')";
        statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, SQLParserFeature.TDDLHint);
        stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        //assertEquals("SELECT trim('x')", stmt.toString());


        outParameters = new ArrayList<Object>();
        parameterizeSql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, outParameters,
                VisitorFeature.OutputParameterizedQuesUnMergeInList,
                VisitorFeature.OutputParameterizedUnMergeShardingTable,
                VisitorFeature.OutputParameterizedQuesUnMergeValuesList);

        System.out.println(parameterizeSql);
        System.out.println(outParameters);
        sql = "select trim(TRAILING 'x' from 'xxxxxxxdxx')";
        statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, SQLParserFeature.TDDLHint);
        stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        //assertEquals("SELECT trim('x')", stmt.toString());


        outParameters = new ArrayList<Object>();
        parameterizeSql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, outParameters,
                VisitorFeature.OutputParameterizedQuesUnMergeInList,
                VisitorFeature.OutputParameterizedUnMergeShardingTable,
                VisitorFeature.OutputParameterizedQuesUnMergeValuesList);

        System.out.println(parameterizeSql);
        System.out.println(outParameters);
    }

}