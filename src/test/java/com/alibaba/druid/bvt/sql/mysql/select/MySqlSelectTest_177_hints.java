package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLReplaceStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlExplainStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLType;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class MySqlSelectTest_177_hints extends MysqlTest {

    @Test
    public void testHintAll() throws Exception {
        String [] hintHead = {"/*+TDDL:node('node_name1')*/",
//                "/*+ TDDL({ 'extra' :{ 'MERGE_UNION' : 'false' }, 'type' : 'direct', 'vtab' : 'drds_shard', 'dbid' : 'corona_qatest_1', 'realtabs' :[ 'drds_shard_04', 'drds_shard_05', 'drds_shard_06' ]}) */",
//                "/!+TDDL: node('node_name1')*/",
//                "/!TDDL: MASTER()*/",
//                "/*TDDL: MASTER()*/"
        };
        for (int i = 0; i < hintHead.length; i++) {
            String sql1 = hintHead[i] + "delete" + hintHead[i] + " from table_1 where id < 10;";
            String sql2 = hintHead[i] + "insert" + hintHead[i] + " into table_1 values('id < 10');";
            String sql3 = hintHead[i] + "update" + hintHead[i] + " table_1 set id = 1 where id < 10;";
            String sql4 = "explain " + hintHead[i] + "delete" + hintHead[i] + " from table_1 where id < 10;";
            String sql5 = "explain " + hintHead[i] + "insert" + hintHead[i] + " into table_1 values('id < 10');";
            String sql6 = "explain " + hintHead[i] + "update" + hintHead[i] + " table_1 set id = 1 where id < 10;";
            String sql7 = hintHead[i] + "replace" + hintHead[i] + " into table_1 values('id < 10');";
            String sql[] = {sql1,sql2,sql7,sql3,sql4,sql5,sql6};
            for (int j = 0; j < sql.length; j++) {
                System.out.println(sql[j]);
                String parameterizedSql = ParameterizedOutputVisitorUtils.parameterizeForTDDL(sql[j],  DbType.mysql,new ArrayList<Object>(),VisitorFeature.OutputParameterizedQuesUnMergeInList,
                        VisitorFeature.OutputParameterizedUnMergeShardingTable,
                        VisitorFeature.OutputParameterizedQuesUnMergeValuesList,
                        VisitorFeature.OutputParameterizedQuesUnMergeOr);
                MySqlStatementParser parser = new MySqlStatementParser(parameterizedSql,
                        SQLParserFeature.TDDLHint,
                        SQLParserFeature.IgnoreNameQuotes);
                try {
                    List<SQLStatement> stmtList = parser.parseStatementList();
                    for (SQLStatement statement : stmtList) {
                        if (statement instanceof MySqlInsertStatement) {
                            Assert.assertNotNull(((MySqlInsertStatement) statement).getHeadHintsDirect().size() > 0);
                            Assert.assertNotNull(((MySqlInsertStatement) statement).getHint() != null);
                            System.out.println(((MySqlInsertStatement) statement).getHeadHintsDirect());
                            System.out.println(((MySqlInsertStatement) statement).getHint());
                        } else if (statement instanceof SQLReplaceStatement) {
                            Assert.assertNotNull(((SQLReplaceStatement) statement).getHeadHintsDirect().size() > 0);
                            Assert.assertNotNull(((SQLReplaceStatement) statement).getHints().size() > 0);
                            System.out.println(((SQLReplaceStatement) statement).getHeadHintsDirect());
                            System.out.println(((SQLReplaceStatement) statement).getHints());
                        } else if (statement instanceof MySqlDeleteStatement) {
                            Assert.assertNotNull(((MySqlDeleteStatement) statement).getHeadHintsDirect().size() > 0);
                            Assert.assertNotNull(((MySqlDeleteStatement) statement).getHints().size() > 0);
                            System.out.println(((MySqlDeleteStatement) statement).getHeadHintsDirect());
                            System.out.println(((MySqlDeleteStatement) statement).getHints());
                        } else if(statement instanceof MySqlUpdateStatement) {
                            Assert.assertNotNull(((MySqlUpdateStatement) statement).getHeadHintsDirect().size() > 0);
                            Assert.assertNotNull(((MySqlUpdateStatement) statement).getHints().size() > 0);
                            System.out.println(((MySqlUpdateStatement) statement).getHeadHintsDirect());
                            System.out.println(((MySqlUpdateStatement) statement).getHints());
                        } else if(statement instanceof MySqlExplainStatement) {
                            SQLStatement statement1 = ((MySqlExplainStatement) statement).getStatement();
                            if (statement1 instanceof MySqlInsertStatement) {
                                Assert.assertNotNull(((MySqlInsertStatement) statement1).getHint() != null);
                                System.out.println(((MySqlInsertStatement) statement1).getHeadHintsDirect());
                                System.out.println(((MySqlInsertStatement) statement1).getHint());
                            } else if (statement1 instanceof MySqlDeleteStatement) {
                                Assert.assertNotNull(((MySqlDeleteStatement) statement1).getHints().size() > 0);
                                System.out.println(((MySqlDeleteStatement) statement1).getHeadHintsDirect());
                                System.out.println(((MySqlDeleteStatement) statement1).getHints());
                            } else if(statement1 instanceof MySqlUpdateStatement) {
                                Assert.assertNotNull(((MySqlUpdateStatement) statement1).getHints().size() > 0);
                                System.out.println(((MySqlUpdateStatement) statement1).getHeadHintsDirect());
                                System.out.println(((MySqlUpdateStatement) statement1).getHints());
                            }
                            Assert.assertNotNull(((MySqlExplainStatement) statement).getHints().size() > 0);
                            System.out.println(((MySqlExplainStatement) statement).getHints());
                        }
                    }
                } catch (com.alibaba.druid.sql.parser.ParserException e) {
                    throw e;
                }
            }

        }

    }


}