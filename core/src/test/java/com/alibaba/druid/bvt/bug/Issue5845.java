package com.alibaba.druid.bvt.bug;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;

import junit.framework.TestCase;

/**
 *
 */
public class Issue5845 extends TestCase {

    public void test_for_issue() throws Exception {
        String sql = "delete from table01 t where t.id=1";
        List<DbType> dbTypes = new ArrayList<>();
        for (DbType dbType : DbType.values()) {
            try {
                String mergeSql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType);
                System.out.println(dbType + "==" + mergeSql);
                dbTypes.add(dbType);
            } catch (Exception e) {
                System.out.println(dbType + "==" + e.getMessage());
            }
        }
        for (DbType dbT : dbTypes) {
            System.out.println("DbType." + dbT + ",");
        }
        for (DbType dbType : new DbType[]{DbType.db2,
            DbType.postgresql,
            DbType.oracle,
            DbType.mysql,
            DbType.mariadb,
            DbType.oceanbase,
            DbType.edb,
            DbType.elastic_search,
            DbType.drds,
            DbType.oceanbase_oracle,
            DbType.greenplum,
            DbType.gaussdb,
            DbType.tidb,
            DbType.goldendb,}) {
            String mergeSql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType);
            System.out.println(dbType + "==" + mergeSql);
            dbTypes.add(dbType);
        }
    }
}
