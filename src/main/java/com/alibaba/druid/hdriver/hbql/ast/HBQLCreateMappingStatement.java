package com.alibaba.druid.hdriver.hbql.ast;

import com.alibaba.druid.hdriver.mapping.HMappingTable;
import com.alibaba.druid.sql.ast.SQLStatementImpl;

public class HBQLCreateMappingStatement extends SQLStatementImpl implements HBQLStatement {

    private static final long serialVersionUID = 1L;

    private HMappingTable          mapping          = new HMappingTable();

    public HMappingTable getMapping() {
        return mapping;
    }

    public void setMapping(HMappingTable mapping) {
        this.mapping = mapping;
    }

}
