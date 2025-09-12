package com.alibaba.druid.bvt.sql.synapse;

import com.alibaba.druid.DbType;
import com.alibaba.druid.bvt.sql.SQLResourceTest;
import org.junit.Test;

public class SynapseResourceTest extends SQLResourceTest {
    public SynapseResourceTest() {
        super(DbType.synapse);
    }

    @Test
    public void synapse_parse() throws Exception {
        fileTest(0, 999, i -> "bvt/parser/synapse/" + i + ".txt");
    }
}
