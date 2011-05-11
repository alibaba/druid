package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLDataTypeImpl;

@SuppressWarnings("serial")
public class SQLCharactorDataType extends SQLDataTypeImpl {
    private String charSetName;
    private String collate;

    public SQLCharactorDataType(String name) {
        super(name);
    }

    public String getCharSetName() {
        return charSetName;
    }

    public void setCharSetName(String charSetName) {
        this.charSetName = charSetName;
    }

    public String getCollate() {
        return collate;
    }

    public void setCollate(String collate) {
        this.collate = collate;
    }

}
