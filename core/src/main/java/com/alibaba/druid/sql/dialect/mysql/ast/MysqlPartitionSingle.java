package com.alibaba.druid.sql.dialect.mysql.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLPartitionSingle;
import com.alibaba.druid.sql.ast.SQLSubPartition;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MysqlPartitionSingle extends SQLPartitionSingle implements MySqlObject {
    protected SQLExpr dataDirectory;
    protected SQLExpr indexDirectory;
    protected SQLExpr maxRows;
    protected SQLExpr minRows;
    protected SQLExpr engine;
    protected SQLExpr comment;

    public SQLExpr getIndexDirectory() {
        return indexDirectory;
    }

    public void setIndexDirectory(SQLExpr indexDirectory) {
        if (indexDirectory != null) {
            indexDirectory.setParent(this);
        }
        this.indexDirectory = indexDirectory;
    }

    public SQLExpr getDataDirectory() {
        return dataDirectory;
    }

    public void setDataDirectory(SQLExpr dataDirectory) {
        if (dataDirectory != null) {
            dataDirectory.setParent(this);
        }
        this.dataDirectory = dataDirectory;
    }

    public SQLExpr getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(SQLExpr maxRows) {
        if (maxRows != null) {
            maxRows.setParent(this);
        }
        this.maxRows = maxRows;
    }

    public SQLExpr getMinRows() {
        return minRows;
    }

    public void setMinRows(SQLExpr minRows) {
        if (minRows != null) {
            minRows.setParent(this);
        }
        this.minRows = minRows;
    }

    public SQLExpr getEngine() {
        return engine;
    }

    public void setEngine(SQLExpr engine) {
        if (engine != null) {
            engine.setParent(this);
        }
        this.engine = engine;
    }

    public SQLExpr getComment() {
        return comment;
    }

    public void setComment(SQLExpr comment) {
        if (comment != null) {
            comment.setParent(this);
        }
        this.comment = comment;
    }

    @Override
    public MysqlPartitionSingle clone() {
        MysqlPartitionSingle x = new MysqlPartitionSingle();

        if (name != null) {
            x.setName(name.clone());
        }

        if (subPartitionsCount != null) {
            x.setSubPartitionsCount(subPartitionsCount.clone());
        }

        for (SQLSubPartition p : subPartitions) {
            SQLSubPartition p2 = p.clone();
            p2.setParent(x);
            x.subPartitions.add(p2);
        }

        if (values != null) {
            x.setValues(values.clone());
        }

        if (dataDirectory != null) {
            x.setDataDirectory(dataDirectory.clone());
        }
        if (indexDirectory != null) {
            x.setDataDirectory(indexDirectory.clone());
        }
        if (maxRows != null) {
            x.setDataDirectory(maxRows.clone());
        }
        if (minRows != null) {
            x.setDataDirectory(minRows.clone());
        }
        if (engine != null) {
            x.setDataDirectory(engine.clone());
        }
        if (comment != null) {
            x.setDataDirectory(comment.clone());
        }

        return x;
    }

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, values);
            acceptChild(visitor, dataDirectory);
            acceptChild(visitor, indexDirectory);
            acceptChild(visitor, maxRows);
            acceptChild(visitor, minRows);
            acceptChild(visitor, engine);
            acceptChild(visitor, comment);
            acceptChild(visitor, subPartitionsCount);
            acceptChild(visitor, subPartitions);
            acceptChild(visitor, locality);
            acceptChild(visitor, tablespace);
        }
        visitor.endVisit(this);
    }
}
