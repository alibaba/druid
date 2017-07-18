package com.alibaba.druid.sql.calcite;

import com.alibaba.druid.support.calcite.DDLSchema;
import junit.framework.TestCase;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.RelBuilder;

/**
 * Created by wenshao on 03/07/2017.
 */
public class CalciteTest extends TestCase {
    public void test_calcite() throws Exception {
        DDLSchema schema = new DDLSchema();

        final FrameworkConfig config = Frameworks
                .newConfigBuilder()
                .defaultSchema(schema)
                .build();

        final RelBuilder builder = RelBuilder.create(config);
        final RelNode node = builder
                .scan("EMP")
                .build();
        System.out.println(RelOptUtil.toString(node));

    }
}
