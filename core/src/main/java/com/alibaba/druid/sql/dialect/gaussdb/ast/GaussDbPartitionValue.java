package com.alibaba.druid.sql.dialect.gaussdb.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLPartitionValue;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class GaussDbPartitionValue extends SQLPartitionValue {
    private SQLExpr spaceName;
    private SQLExpr dataNode;
    private SQLExpr start;
    private SQLExpr end;
    private SQLExpr every;
    private boolean isDistribute;

    public GaussDbPartitionValue(Operator operator) {
        super();
        this.operator = operator;
    }

    public void setSpaceName(SQLExpr spaceName) {
        if (spaceName != null) {
            spaceName.setParent(this);
        }
        this.spaceName = spaceName;
    }

    public SQLExpr getSpaceName() {
        return spaceName;
    }

    public void setDataNodes(SQLExpr dataNode) {
        if (dataNode != null) {
            dataNode.setParent(this);
        }
        this.dataNode = dataNode;
    }

    public SQLExpr getDataNode() {
        return dataNode;
    }

    public void setStart(SQLExpr start) {
        if (start != null) {
            start.setParent(this);
        }
        this.start = start;
    }

    public SQLExpr getStart() {
        return start;
    }

    public void setEnd(SQLExpr end) {
        if (end != null) {
            end.setParent(this);
        }
        this.end = end;
    }

    public SQLExpr getEnd() {
        return end;
    }

    public void setEvery(SQLExpr every) {
        if (every != null) {
            every.setParent(this);
        }
        this.every = every;
    }

    public SQLExpr getEvery() {
        return every;
    }

    public void setDistribute(boolean isDistribute) {
        this.isDistribute = isDistribute;
    }

    public boolean getDistribute() {
        return isDistribute;
    }

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, start);
            acceptChild(visitor, end);
            acceptChild(visitor, every);
            acceptChild(visitor, spaceName);
            acceptChild(visitor, dataNode);
            visitor.endVisit(this);
        }
    }
}
