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
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class SQLColumnDefinition extends SQLObjectImpl implements SQLTableElement, SQLObjectWithDataType, SQLReplaceable, SQLDbTypedObject {
    protected String                          dbType;

    protected SQLName                         name;
    protected SQLDataType                     dataType;
    protected SQLExpr                         defaultExpr;
    protected final List<SQLColumnConstraint> constraints   = new ArrayList<SQLColumnConstraint>(0);
    protected SQLExpr                         comment;

    protected Boolean                         enable;
    protected Boolean                         validate;
    protected Boolean                         rely;

    // for mysql
    protected boolean                         autoIncrement = false;
    protected SQLExpr                         onUpdate;
    protected SQLExpr                         format;
    protected SQLExpr                         storage;
    protected SQLExpr                         charsetExpr;
    protected SQLExpr                         collateExpr;
    protected SQLExpr                         asExpr;
    protected boolean                         stored        = false;
    protected boolean                         virtual       = false;
    protected boolean                         visible       = false;
    protected AutoIncrementType               sequenceType;
    protected boolean                         preSort       = false; // for ads
    protected int                             preSortOrder  = 0; // for ads

    protected Identity                        identity;

    // for ads
    protected SQLExpr                         generatedAlawsAs;
    protected SQLExpr                         delimiter; // for ads
    protected SQLExpr                         delimiterTokenizer; // for ads3.0 multivalue
    protected SQLExpr                         nlpTokenizer; // for ads3.0 multivalue
    protected SQLExpr                         valueType; // for ads3.0 multivalue
    protected boolean                         disableIndex  = false; //for ads
    protected SQLExpr                         jsonIndexAttrsExpr;    // for ads
    private SQLExpr                           unitCount;
    private SQLExpr                           unitIndex;
    private SQLExpr                           step;
    private SQLCharExpr                       encode;
    private SQLCharExpr                       compression;

    // for aliyun data lake anlytics
    private List<SQLAssignItem>               mappedBy;
    private List<SQLAssignItem>               colProperties;

    public SQLColumnDefinition(){

    }

    public Identity getIdentity() {
        return identity;
    }

    // for sqlserver
    public void setIdentity(Identity identity) {
        if (identity != null) {
            identity.setParent(this);
        }
        this.identity = identity;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Boolean getValidate() {
        return validate;
    }

    public void setValidate(Boolean validate) {
        this.validate = validate;
    }

    public Boolean getRely() {
        return rely;
    }

    public void setRely(Boolean rely) {
        this.rely = rely;
    }

    public SQLName getName() {
        return name;
    }

    public String getColumnName() {
        if (name == null) {
            return null;
        }

        return name.getSimpleName();
    }

    public long nameHashCode64() {
        if (name == null) {
            return 0;
        }

        return name.hashCode64();
    }

    public String getNameAsString() {
        if (name == null) {
            return null;
        }

        return name.toString();
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public void setName(String name) {
        this.setName(new SQLIdentifierExpr(name));
    }

    public SQLDataType getDataType() {
        return dataType;
    }
//
//    public int jdbcType() {
//        if (dataType == null) {
//            return Types.OTHER;
//        }
//
//        return dataType.jdbcType();
//    }

    public void setDataType(SQLDataType dataType) {
        if (dataType != null) {
            dataType.setParent(this);
        }
        this.dataType = dataType;
    }

    public SQLExpr getDefaultExpr() {
        return defaultExpr;
    }

    public void setDefaultExpr(SQLExpr defaultExpr) {
        if (defaultExpr != null) {
            defaultExpr.setParent(this);
        }
        this.defaultExpr = defaultExpr;
    }

    public List<SQLColumnConstraint> getConstraints() {
        return constraints;
    }

    public boolean isPrimaryKey() {
        for (SQLColumnConstraint constraint : constraints) {
            if (constraint instanceof SQLColumnPrimaryKey) {
                return true;
            }
        }

        if (parent instanceof SQLCreateTableStatement) {
            return ((SQLCreateTableStatement) parent)
                    .isPrimaryColumn(
                            nameHashCode64());
        }

        return false;
    }

    public boolean isOnlyPrimaryKey() {
        for (SQLColumnConstraint constraint : constraints) {
            if (constraint instanceof SQLColumnPrimaryKey) {
                return true;
            }
        }

        if (parent instanceof SQLCreateTableStatement) {
            return ((SQLCreateTableStatement) parent)
                    .isPrimaryColumn(
                            nameHashCode64());
        }

        return false;
    }

    public boolean isPartitionBy() {
        if (!(parent instanceof SQLCreateTableStatement)) {
            return false;
        }

        SQLCreateTableStatement stmt = (SQLCreateTableStatement) parent;
        final SQLPartitionBy partitioning = stmt.getPartitioning();
        if (partitioning == null) {
            return false;
        }

        if (name == null) {
            return false;
        }

        return partitioning.isPartitionByColumn(
                nameHashCode64());
    }

    public void addConstraint(SQLColumnConstraint constraint) {
        if (constraint != null) {
            constraint.setParent(this);
        }
        this.constraints.add(constraint);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, name);
            this.acceptChild(visitor, dataType);
            this.acceptChild(visitor, defaultExpr);
            this.acceptChild(visitor, constraints);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getComment() {
        return comment;
    }

    public void setComment(String comment) {
        SQLCharExpr expr;
        if (comment == null) {
            expr = null;
        } else {
            expr = new SQLCharExpr(comment);
        }
        this.setComment(expr);
    }

    public void setComment(SQLExpr comment) {
        if (comment != null) {
            comment.setParent(this);
        }
        this.comment = comment;
    }

    public boolean isVirtual() {
        return virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

    public boolean isStored() {
        return stored;
    }

    public void setStored(boolean stored) {
        this.stored = stored;
    }

    public SQLExpr getCharsetExpr() {
        return charsetExpr;
    }

    public void setCharsetExpr(SQLExpr charsetExpr) {
        if (charsetExpr != null) {
            charsetExpr.setParent(this);
        }
        this.charsetExpr = charsetExpr;
    }

    public SQLExpr getCollateExpr() {
        return collateExpr;
    }

    public void setCollateExpr(SQLExpr x) {
        if (charsetExpr != null) {
            charsetExpr.setParent(this);
        }
        this.collateExpr = x;
    }

    public SQLExpr getAsExpr() {
        return asExpr;
    }

    public void setAsExpr(SQLExpr asExpr) {
        if (charsetExpr != null) {
            charsetExpr.setParent(this);
        }
        this.asExpr = asExpr;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public SQLExpr getOnUpdate() {
        return onUpdate;
    }

    public void setOnUpdate(SQLExpr onUpdate) {
        this.onUpdate = onUpdate;
    }

    public SQLExpr getFormat() {
        return format;
    }

    public void setFormat(SQLExpr format) {
        this.format = format;
    }

    public SQLExpr getStorage() {
        return storage;
    }

    public void setStorage(SQLExpr storage) {
        this.storage = storage;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (defaultExpr == expr) {
            setDefaultExpr(target);
            return true;
        }

        if (name == expr) {
            setName((SQLName) target);
            return true;
        }

        if (comment == expr) {
            setComment(target);
            return true;
        }

        return false;
    }

    public void setUnitCount(SQLExpr unitCount) {
        if (unitCount != null) {
            unitCount.setParent(this);
        }
        this.unitCount = unitCount;
    }

    public static class Identity extends SQLObjectImpl {

        private Integer seed;
        private Integer increment;

        private boolean notForReplication;
        private boolean cycle;

        private Integer minValue;
        private Integer maxValue;

        public Identity(){

        }

        public Integer getSeed() {
            return seed;
        }

        public void setSeed(Integer seed) {
            this.seed = seed;
        }

        public Integer getIncrement() {
            return increment;
        }

        public void setIncrement(Integer increment) {
            this.increment = increment;
        }

        public boolean isCycle() {
            return cycle;
        }

        public void setCycle(boolean cycle) {
            this.cycle = cycle;
        }

        public Integer getMinValue() {
            return minValue;
        }

        public void setMinValue(Integer minValue) {
            this.minValue = minValue;
        }

        public Integer getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(Integer maxValue) {
            this.maxValue = maxValue;
        }

        public boolean isNotForReplication() {
            return notForReplication;
        }

        public void setNotForReplication(boolean notForReplication) {
            this.notForReplication = notForReplication;
        }

        @Override
        public void accept0(SQLASTVisitor visitor) {
            visitor.visit(this);
            visitor.endVisit(this);
        }

        public Identity clone () {
            Identity x = new Identity();
            x.seed = seed;
            x.increment = increment;
            x.cycle = cycle;
            x.minValue = minValue;
            x.maxValue = maxValue;
            x.notForReplication = notForReplication;
            return x;
        }
    }

    public String computeAlias() {
        String alias = null;

        if (name instanceof SQLIdentifierExpr) {
            alias = ((SQLIdentifierExpr) name).getName();
        } else if (name instanceof SQLPropertyExpr) {
            alias = ((SQLPropertyExpr) name).getName();
        }

        return SQLUtils.normalize(alias);
    }

    public SQLColumnDefinition clone() {
        SQLColumnDefinition x = new SQLColumnDefinition();
        x.setDbType(dbType);

        if(name != null) {
            x.setName(name.clone());
        }

        if (dataType != null) {
            x.setDataType(dataType.clone());
        }

        if (defaultExpr != null) {
            x.setDefaultExpr(defaultExpr.clone());
        }

        for (SQLColumnConstraint item : constraints) {
            SQLColumnConstraint itemCloned = item.clone();
            itemCloned.setParent(x);
            x.constraints.add(itemCloned);
        }

        if (comment != null) {
            x.setComment(comment.clone());
        }

        x.enable = enable;
        x.validate = validate;
        x.rely = rely;

        x.autoIncrement = autoIncrement;

        if (onUpdate != null) {
            x.setOnUpdate(onUpdate.clone());
        }

        if (format != null) {
            x.setFormat(format.clone());
        }

        if (storage != null) {
            x.setStorage(storage.clone());
        }

        if (charsetExpr != null) {
            x.setCharsetExpr(charsetExpr.clone());
        }

        if (collateExpr != null) {
            x.setCollateExpr(collateExpr.clone());
        }

        if (asExpr != null) {
            x.setAsExpr(asExpr.clone());
        }

        x.stored = stored;
        x.virtual = virtual;

        if (identity != null) {
            x.setIdentity(identity.clone());
        }

        if (delimiter != null) {
            x.setDelimiter(delimiter.clone());
        }

        if (valueType != null) {
            x.setValueType(valueType.clone());
        }

        if (nlpTokenizer != null) {
            x.setNplTokenizer(nlpTokenizer.clone());
        }

        x.preSort = preSort;
        x.preSortOrder = preSortOrder;

        if (jsonIndexAttrsExpr != null) {
            x.setJsonIndexAttrsExpr(jsonIndexAttrsExpr.clone());
        }

        if (mappedBy != null) {
            for (SQLAssignItem item : mappedBy) {
                SQLAssignItem item2 = item.clone();
                item2.setParent(this);
                if (x.mappedBy == null) {
                    x.mappedBy = new ArrayList<SQLAssignItem>();
                }
                x.mappedBy.add(item2);
            }
        }

        if (colProperties != null) {
            for (SQLAssignItem item : colProperties) {
                SQLAssignItem item2 = item.clone();
                item2.setParent(this);
                if (x.colProperties == null) {
                    x.colProperties = new ArrayList<SQLAssignItem>();
                }
                x.colProperties.add(item2);
            }
        }

        return x;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public void simplify() {
        enable = null;
        validate = null;
        rely = null;


        if (this.name instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr identExpr = (SQLIdentifierExpr) this.name;
            String columnName = identExpr.getName();
            String normalized = SQLUtils.normalize(columnName, dbType);
            if (normalized != columnName) {
                this.setName(normalized);
            }
        }
    }

    public boolean containsNotNullConstaint() {
        for (SQLColumnConstraint constraint : this.constraints) {
            if (constraint instanceof SQLNotNullConstraint) {
                return true;
            }
        }

        return false;
    }

    public SQLExpr getGeneratedAlawsAs() {
        return generatedAlawsAs;
    }

    public void setGeneratedAlawsAs(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.generatedAlawsAs = x;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public SQLExpr getDelimiter() {
        return delimiter;
    }

    public boolean isDisableIndex() {
        return disableIndex;
    }

    public void setDisableIndex(boolean disableIndex) {
        this.disableIndex = disableIndex;
    }

    public void setDelimiter(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.delimiter = x;
    }

    public SQLExpr getDelimiterTokenizer() {
        return delimiterTokenizer;
    }

    public void setDelimiterTokenizer(SQLExpr delimiterTokenizer) {
        this.delimiterTokenizer = delimiterTokenizer;
    }

    public SQLExpr getNlpTokenizer() {
        return nlpTokenizer;
    }

    public void setNlpTokenizer(SQLExpr nlpTokenizer) {
        this.nlpTokenizer = nlpTokenizer;
    }

    public SQLExpr getValueType() {
        return valueType;
    }

    public void setValueType(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.valueType = x;
    }

    public boolean isPreSort() {
        return preSort;
    }

    public void setPreSort(boolean preSort) {
        this.preSort = preSort;
    }

    public int getPreSortOrder() {
        return preSortOrder;
    }

    public void setPreSortOrder(int preSortOrder) {
        this.preSortOrder = preSortOrder;
    }

    public SQLExpr getJsonIndexAttrsExpr() {
        return jsonIndexAttrsExpr;
    }

    public void setJsonIndexAttrsExpr(SQLExpr jsonIndexAttrsExpr) {
        this.jsonIndexAttrsExpr = jsonIndexAttrsExpr;
    }

    public AutoIncrementType getSequenceType() {
        return sequenceType;
    }

    public void setSequenceType(AutoIncrementType sequenceType) {
        this.sequenceType = sequenceType;
    }

    public String toString() {
        return SQLUtils.toSQLString(this, dbType);
    }

    public SQLExpr getUnitCount() {
        return unitCount;
    }

    public SQLExpr getUnitIndex() {
        return unitIndex;
    }

    public void setUnitIndex(SQLExpr unitIndex) {
        if (unitIndex != null) {
            unitIndex.setParent(this);
        }
        this.unitIndex = unitIndex;
    }

    public SQLExpr getNplTokenizer() {
        return nlpTokenizer;
    }

    public void setNplTokenizer(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.nlpTokenizer = x;
    }

    public SQLExpr getStep() {
        return step;
    }

    public void setStep(SQLExpr step) {
        if (step != null) {
            step.setParent(this);
        }
        this.step = step;
    }

    public List<SQLAssignItem> getMappedBy() {
        if (mappedBy == null) {
            mappedBy = new ArrayList<SQLAssignItem>();
        }
        return mappedBy;
    }

    public List<SQLAssignItem> getMappedByDirect() {
        return mappedBy;
    }

    public List<SQLAssignItem> getColProperties() {
        if (colProperties == null) {
            colProperties = new ArrayList<SQLAssignItem>();
        }
        return colProperties;
    }

    public SQLCharExpr getEncode() {
        return encode;
    }

    public void setEncode(SQLCharExpr encode) {
        this.encode = encode;
    }

    public SQLCharExpr getCompression() {
        return compression;
    }

    public void setCompression(SQLCharExpr compression) {
        this.compression = compression;
    }

    public List<SQLAssignItem> getColPropertiesDirect() {
        return colProperties;
    }

}
