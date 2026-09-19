package com.alibaba.druid.bvt.sql.mysql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @see <a href="https://github.com/alibaba/druid/issues/6661">Issue #6661</a>
 */
public class Issue6661 {
    private static final String SQL =
            "SELECT * FROM `his_0093`.`basedisease` WHERE aa = '123'";
    private static final String EXPECTED =
            "SELECT *\n"
                    + "FROM `his_0093`.`basedisease`\n"
                    + "WHERE aa = ?";

    @Test
    public void test_parameterize_preserves_quoted_schema_name() {
        String parameterized = ParameterizedOutputVisitorUtils.parameterize(SQL, DbType.mysql);

        assertEquals(EXPECTED, parameterized);
    }

    @Test
    public void test_parameterize_merges_schema_when_table_is_sharded() {
        String sql = "SELECT * FROM `his_0093`.`basedisease_0001` WHERE aa = '123'";
        String parameterized = ParameterizedOutputVisitorUtils.parameterize(sql, DbType.mysql);

        assertEquals(
                "SELECT *\n"
                        + "FROM his.basedisease\n"
                        + "WHERE aa = ?",
                parameterized
        );
    }
}
