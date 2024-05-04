package com.alibaba.druid.bvt.bug;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;

import junit.framework.TestCase;

/**
 *
 */
public class Issue5847 extends TestCase {

    public void test_for_issue() throws Exception {
        String sql = "-- 执行SQLUtils.format(sql, DbType.dm)结果，外层括号被剔除，该sql在dm库执行失败\n"
            + "SELECT *\n"
            + "FROM tb_test\n"
            + "LIMIT 10 OFFSET ( (2 - 1) * 1 *( 3 + 5) )";

        sql="select a.*, (a.swanav-lead(a.swanav,1,null::numeric) over w)/lead(a.swanav,1,null::numeric) over w as roe_lag\n";
        sql="select a.*, ((a.swanav-lead(a.swanav,1,null::numeric) over w)/lead(a.swanav,1,null::numeric) over w) as roe_lag\n";
        sql="select * from aaa "

       + "group by to_char((CreateDate || ' ' || cast(HourArgment as VARCHAR) || ':00:00')::TIMESTAMP, 'YYYY-MM-DD HH24')";

        for (DbType dbType : new DbType[]{
            //DbType.db2,
            DbType.postgresql,
//            DbType.oracle,
//            DbType.mysql,
//            DbType.mariadb,
//            DbType.oceanbase,
//            DbType.edb,
//            DbType.elastic_search,
//            DbType.drds,
//            DbType.oceanbase_oracle,
//            DbType.greenplum,
//            DbType.gaussdb,
//            DbType.tidb,
//            DbType.goldendb,
            //DbType.dm,

        }) {
            try {
               // String mergeSql = SQLUtils.format(sql, dbType);
                List<SQLStatement> list = SQLUtils.parseStatements(sql, dbType);
                System.out.println(dbType + "==" + list);
                SQLBinaryOpExpr fff;
            } catch (Exception e) {
                System.out.println(dbType + "==" + e.getMessage());
            }
        }
    }
}
