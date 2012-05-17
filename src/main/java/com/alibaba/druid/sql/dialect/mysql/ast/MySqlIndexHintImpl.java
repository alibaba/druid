package com.alibaba.druid.sql.dialect.mysql.ast;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public abstract class MySqlIndexHintImpl extends MySqlObjectImpl implements MySqlIndexHint {

    private static final long     serialVersionUID = 1L;

    private MySqlIndexHint.Option option;

    private List<SQLName>         indexList        = new ArrayList<SQLName>();

    @Override
    public abstract void accept0(MySqlASTVisitor visitor);

    public MySqlIndexHint.Option getOption() {
        return option;
    }

    public void setOption(MySqlIndexHint.Option option) {
        this.option = option;
    }

    public List<SQLName> getIndexList() {
        return indexList;
    }

    public void setIndexList(List<SQLName> indexList) {
        this.indexList = indexList;
    }

}
