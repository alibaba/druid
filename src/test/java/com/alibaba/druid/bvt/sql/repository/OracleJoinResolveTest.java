package com.alibaba.druid.bvt.sql.repository;

import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

/**
 * Created by wenshao on 03/08/2017.
 */
public class OracleJoinResolveTest extends TestCase {
    protected SchemaRepository repository = new SchemaRepository(JdbcConstants.ORACLE);

    public void test_for_issue() throws Exception {
        repository.console("create table t_user (uid number(20, 0), gid number(20, 0), uname varchar2(20))");
        repository.console("create table t_group (id number(20, 0), name varchar2(20))");

        assertEquals("SELECT a.uid, a.gid, a.uname\n" +
                "FROM t_user a\n" +
                "\tINNER JOIN t_group b\n" +
                "WHERE a.uid = b.id"
                , repository.resolve("select a.* from t_user a inner join t_group b where a.uid = id"));
    }
}
