package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_35 extends TestCase {
    public void test_for_parameterize() throws Exception {
        final DbType dbType = JdbcConstants.MYSQL;


        String sql = "/*+TDDL({'extra':{'SOCKET_TIMEOUT':'3600000'}})*/\n" +
                "select sample_table_schema, \n" +
                "sample_table_name,\n" +
                "sample_table_orig_size,\n" +
                "sample_table_sample_size,\n" +
                "col.id as id,\n" +
                "sample_column_name, \n" +
                "sample_column_type,\n" +
                "sample_string, \n" +
                "sample_column_highkey, \n" +
                "sample_column_high2key, \n" +
                "sample_column_lowkey,\n" +
                "sample_column_low2key,\n" +
                "sample_column_cardinality,\n" +
                "sample_avg_length,\n" +
                "sample_column_dist_type as type, \n" +
                "sample_column_dist_quantileno as quantileno,\n" +
                "sample_column_dist_highkey,\n" +
                "sample_column_dist_lowkey, \n" +
                "sample_column_dist_value,\n" +
                "sample_column_dist_cardinality,\n" +
                "sample_column_dist_count,\n" +
                "col.gmt_create as time\n" +
                "from sample_tables tab, sample_columns col, sample_column_distribution dist\n" +
                "where \n" +
                "tab.id = col.sample_column_table_id and  \n" +
                "col.id = dist.sample_column_dist_column_id and \n" +
                "col.id = ( \n" +
                "    SELECT id FROM sample_columns col \n" +
                "    WHERE sample_column_name = 'gmt_modified'\n" +
                "    AND sample_column_table_schema = 'SC_PRODUCT_03'\n" +
                "    AND sample_column_table_name = 'product_0096'\n" +
                "    ORDER BY id DESC LIMIT 1 \n" +
                "    order by type, quantileno)";



        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLStatement statement = stmtList.get(0);

        StringBuilder out = new StringBuilder();
        //  List<Object> parameters = new ArrayList<Object>();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, JdbcConstants.MYSQL);
        visitor.setParameterized(true);
        visitor.setParameterizedMergeInList(true);
        //   visitor.setParameters(parameters);
        visitor.setExportTables(true);
        visitor.setPrettyFormat(false);
        statement.accept(visitor);
        System.out.println(out);
    }
}
