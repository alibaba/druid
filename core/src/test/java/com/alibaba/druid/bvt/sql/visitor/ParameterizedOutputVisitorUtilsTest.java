package com.alibaba.druid.bvt.sql.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ParameterizedOutputVisitorUtilsTest {
    @Test
    public void test() {
        String sql = "select * from t where id = ?";
        assertEquals(sql, ParameterizedOutputVisitorUtils.parameterize(sql, DbType.mysql));
    }

    @Test
    public void test_x() {
        String sql = "select data_uuid as uuid, data_id as id from m_index WHERE tenant_id = ? and namespace = ? and meta_object_uuid = ? and delete_tag = ? and (value101 in (?) and value104 is not null) order by data_id desc";
        assertEquals(sql, ParameterizedOutputVisitorUtils.parameterize(sql, DbType.mysql, (List<Object>) null));
    }

    @Test
    public void test1() {
        String sql = "select * from t where id = ? and flag = 0";
        assertEquals(sql, ParameterizedOutputVisitorUtils.parameterize(sql, DbType.mysql, VisitorFeature.OutputParameterizedUnMergeZero));
    }

    @Test
    public void test2() {
        String sql = "select * from t where id = ? and flag = 1";
        assertEquals(sql, ParameterizedOutputVisitorUtils.parameterize(sql, DbType.mysql, VisitorFeature.OutputParameterizedUnMergeOne));
    }
}
