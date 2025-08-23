package com.alibaba.druid.bvt.proxy.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.alibaba.druid.DbType;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;


import com.alibaba.druid.filter.stat.StatFilter;

public class StatFilterTest3 extends TestCase {
    @SuppressWarnings("deprecation")
    public void test_dbType() throws Exception {
        StatFilter filter = new StatFilter();

        assertFalse(filter.isMergeSql());

        filter.setDbType("mysql");
        filter.setMergeSql(true);

        assertTrue(filter.isMergeSql());
        assertEquals(DbType.mysql, filter.getDbType());

        assertEquals("SELECT ?\nLIMIT ?", filter.mergeSql("select 'x' limit 1"));
    }

    public void test_dbType_error() throws Exception {
        StatFilter filter = new StatFilter();
        filter.setDbType("mysql");
        filter.setMergeSql(true);

        assertEquals(DbType.mysql, filter.getDbType());

        assertEquals("sdafawer asf ", filter.mergeSql("sdafawer asf "));
    }

    public void test_merge() throws Exception {
        StatFilter filter = new StatFilter();
        filter.setDbType("mysql");
        filter.setMergeSql(false);

        assertEquals(DbType.mysql, filter.getDbType());

        assertEquals("select 'x' limit 1", filter.mergeSql("select 'x' limit 1"));
    }


    public void test_merge_pg() throws Exception {
        StatFilter filter = new StatFilter();
        filter.setDbType(JdbcConstants.POSTGRESQL);
        filter.setMergeSql(true);

        assertEquals(JdbcConstants.POSTGRESQL, filter.getDbType());

        assertEquals("DROP TABLE IF EXISTS test_site_data_select_111;\n" +
                "CREATE TABLE test_site_data_select_111\n" +
                "AS\n" +
                "SELECT *\n" +
                "FROM postman_trace_info_one\n" +
                "WHERE lng > ?\n" +
                "\tAND lat > ?\n" +
                "\tAND site_id = ?;", filter.mergeSql("drop table if exists test_site_data_select_111; create table test_site_data_select_111 AS select * from postman_trace_info_one  where lng>0 and lat>0  and site_id='17814' ;", JdbcConstants.POSTGRESQL));
    }

    public void test_merge_oracle() throws Exception {
        StatFilter filter = new StatFilter();
        filter.setDbType(DbType.oceanbase_oracle);
        filter.setMergeSql(true);

        filter.mergeSql("insert into t(f1, f2) values (1, 2)", DbType.oceanbase_oracle);
    }

    public void test_merge_nodbtype() throws Exception {
        StatFilter filter = new StatFilter();

        assertFalse(filter.isMergeSql());

        filter.setMergeSql(true);

        assertTrue(filter.isMergeSql());
        assertNull(filter.getDbType());

        assertEquals("SELECT *\n" +
                        "FROM temp.test\n" +
                        "ORDER BY id DESC\n" +
                        "LIMIT ?"
                , filter.mergeSql("select * from temp.test order by id desc limit 1"));
    }
}
