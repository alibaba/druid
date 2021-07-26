package com.alibaba.druid.sql.dialect.dm.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLWindow;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

/**
 * @author two brother
 * @date 2021/7/22 11:05
 */
public class DMSQLSelectQueryBlock extends SQLSelectQueryBlock {
    private int arg0 = -1;
    private int arg1 = -1;
    private boolean PERCENT = false;
    private boolean WITH = false;
    private boolean TIES = false;


    public DMSQLSelectQueryBlock() {
    }

    public DMSQLSelectQueryBlock(DbType dbType) {
        super(dbType);
    }

    public void setArg0(int arg0) {
        this.arg0 = arg0;
    }

    public void setArg1(int arg1) {
        this.arg1 = arg1;
    }

    public int getArg0() {
        return arg0;
    }

    public int getArg1() {
        return arg1;
    }
    public boolean hasTop(){
        return arg0!=-1||arg1!=-1;
    }

    public boolean isPERCENT() {
        return PERCENT;
    }

    public void setPERCENT(boolean PERCENT) {
        this.PERCENT = PERCENT;
    }

    public boolean isWITH() {
        return WITH;
    }

    public void setWITH(boolean WITH) {
        this.WITH = WITH;
    }

    public boolean isTIES() {
        return TIES;
    }

    public void setTIES(boolean TIES) {
        this.TIES = TIES;
    }
}
