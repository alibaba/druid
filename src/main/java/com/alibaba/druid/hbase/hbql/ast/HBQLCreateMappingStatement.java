package com.alibaba.druid.hbase.hbql.ast;

import com.alibaba.druid.hbase.mapping.HMapping;
import com.alibaba.druid.sql.ast.SQLStatementImpl;

public class HBQLCreateMappingStatement extends SQLStatementImpl implements HBQLStatement {

    private static final long serialVersionUID = 1L;

    private HMapping          mapping          = new HMapping();

    public HMapping getMapping() {
        return mapping;
    }

    public void setMapping(HMapping mapping) {
        this.mapping = mapping;
    }

}
