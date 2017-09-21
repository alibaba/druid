
package com.alibaba.druid.bvt.sql.oracle.visitor;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenshao on 16/8/23.
 */
public class OracleParameterizedOutputVisitorTest_01 extends TestCase {
    public void test_for_parameterize() throws Exception {
        String sql = "SELECT dd.synonym_name table_name,'EAIREP' as schema_name,  aa.num_rows,bb.comments description,to_char(cc.created,'YYYY-MM-DD HH24:MI:SS') createTime,to_char(cc.last_ddl_time,'YYYY-MM-DD HH24:MI:SS') last_ddl_time, 'SYNONYM' table_type ,lower(aa.owner || '.' || aa.table_name) ref_info\n" +
                " from dba_synonyms dd,\n" +
                "    dba_objects cc,\n" +
                "    dba_tables aa\n" +
                "  left join    dba_tab_comments bb\n" +
                "    on aa.owner = bb.owner\n" +
                "   and aa.table_name = bb.table_name\n" +
                " where  aa.owner=cc.owner\n" +
                "   and aa.table_name=cc.object_name\n" +
                "   and cc.subobject_name is null\n" +
                "   and cc.object_type='TABLE'\n" +
                "   and aa.owner = dd.table_owner\n" +
                "   and aa.table_name = dd.table_name   and dd.owner = :1  ";
        List<Object> parameters = new ArrayList<Object>();
        parameters.add("EAIREP");
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        String tempResult = SQLUtils.toSQLString(stmts, com.alibaba.druid.util.JdbcConstants.ORACLE, parameters);
        assertEquals("SELECT dd.synonym_name AS table_name, 'EAIREP' AS schema_name, aa.num_rows, bb.comments AS description\n" +
                "\t, to_char(cc.created, 'YYYY-MM-DD HH24:MI:SS') AS createTime\n" +
                "\t, to_char(cc.last_ddl_time, 'YYYY-MM-DD HH24:MI:SS') AS last_ddl_time, 'SYNONYM' AS table_type\n" +
                "\t, lower(aa.owner || '.' || aa.table_name) AS ref_info\n" +
                "FROM dba_synonyms dd, dba_objects cc, dba_tables aa\n" +
                "\tLEFT JOIN dba_tab_comments bb ON aa.owner = bb.owner\n" +
                "AND aa.table_name = bb.table_name \n" +
                "WHERE aa.owner = cc.owner\n" +
                "\tAND aa.table_name = cc.object_name\n" +
                "\tAND cc.subobject_name IS NULL\n" +
                "\tAND cc.object_type = 'TABLE'\n" +
                "\tAND aa.owner = dd.table_owner\n" +
                "\tAND aa.table_name = dd.table_name\n" +
                "\tAND dd.owner = 'EAIREP'", tempResult);
    }

}
