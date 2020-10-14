/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.dialect.oracle.ast.OracleSegmentAttributes;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSegmentAttributesImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLPartition extends OracleSegmentAttributesImpl implements OracleSegmentAttributes {

    protected SQLName               name;

    protected SQLExpr               subPartitionsCount;

    protected List<SQLSubPartition> subPartitions = new ArrayList<SQLSubPartition>();

    protected SQLPartitionValue     values;
    
    // for mysql
    protected SQLExpr           dataDirectory;
    protected SQLExpr           indexDirectory;
    protected SQLExpr           maxRows;
    protected SQLExpr           minRows;
    protected SQLExpr           engine;
    protected SQLExpr           comment;

    // for oracle
    protected boolean segmentCreationImmediate;
    protected boolean segmentCreationDeferred;

    private SQLObject lobStorage;

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }

    public SQLExpr getSubPartitionsCount() {
        return subPartitionsCount;
    }

    public void setSubPartitionsCount(SQLExpr subPartitionsCount) {
        if (subPartitionsCount != null) {
            subPartitionsCount.setParent(this);
        }
        this.subPartitionsCount = subPartitionsCount;
    }

    public SQLPartitionValue getValues() {
        return values;
    }

    public void setValues(SQLPartitionValue values) {
        if (values != null) {
            values.setParent(this);
        }
        this.values = values;
    }

    public List<SQLSubPartition> getSubPartitions() {
        return subPartitions;
    }
    
    public void addSubPartition(SQLSubPartition partition) {
        if (partition != null) {
            partition.setParent(this);
        }
        this.subPartitions.add(partition);
    }

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
    
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, values);
            acceptChild(visitor, dataDirectory);
            acceptChild(visitor, indexDirectory);
            acceptChild(visitor, tablespace);
            acceptChild(visitor, maxRows);
            acceptChild(visitor, minRows);
            acceptChild(visitor, engine);
            acceptChild(visitor, comment);

            acceptChild(visitor, subPartitionsCount);
            acceptChild(visitor, storage);
            acceptChild(visitor, subPartitions);
        }
        visitor.endVisit(this);
    }

    public SQLObject getLobStorage() {
        return lobStorage;
    }

    public void setLobStorage(SQLObject lobStorage) {
        if (lobStorage != null) {
            lobStorage.setParent(this);
        }
        this.lobStorage = lobStorage;
    }

    public boolean isSegmentCreationImmediate() {
        return segmentCreationImmediate;
    }

    public void setSegmentCreationImmediate(boolean segmentCreationImmediate) {
        this.segmentCreationImmediate = segmentCreationImmediate;
    }

    public boolean isSegmentCreationDeferred() {
        return segmentCreationDeferred;
    }

    public void setSegmentCreationDeferred(boolean segmentCreationDeferred) {
        this.segmentCreationDeferred = segmentCreationDeferred;
    }

    public SQLPartition clone() {
        SQLPartition x = new SQLPartition();

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
        x.segmentCreationImmediate = segmentCreationImmediate;
        x.segmentCreationDeferred = segmentCreationDeferred;

        if (lobStorage != null) {
            x.setLobStorage(lobStorage.clone());
        }

        return x;
    }
}
