package com.alibaba.druid.bvt.bug;

import org.junit.Test;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;

import junit.framework.TestCase;

public class Issue_4190 extends TestCase {
    @Test
    public void test_1() {
        String sql = "select Ülke, İsim\n"
                + " from [Örnek VeriTabanı].dbo.[MÜŞteri Bilgisi]";
        SQLStatement actual = SQLUtils.parseSingleStatement(sql, DbType.sqlserver);
        assertNotNull(actual);
    }
}
