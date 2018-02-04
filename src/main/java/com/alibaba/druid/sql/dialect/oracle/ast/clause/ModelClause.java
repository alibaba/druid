/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.oracle.ast.clause;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleExpr;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class ModelClause extends OracleSQLObjectImpl {

    private final List<CellReferenceOption>  cellReferenceOptions  = new ArrayList<CellReferenceOption>();
    private ReturnRowsClause                 returnRowsClause;
    private final List<ReferenceModelClause> referenceModelClauses = new ArrayList<ReferenceModelClause>();
    private MainModelClause                  mainModel;

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, returnRowsClause);
            acceptChild(visitor, referenceModelClauses);
            acceptChild(visitor, mainModel);
        }
        visitor.endVisit(this);
    }

    public MainModelClause getMainModel() {
        return mainModel;
    }

    public void setMainModel(MainModelClause mainModel) {
        this.mainModel = mainModel;
    }

    public ReturnRowsClause getReturnRowsClause() {
        return returnRowsClause;
    }

    public void setReturnRowsClause(ReturnRowsClause returnRowsClause) {
        this.returnRowsClause = returnRowsClause;
    }

    public List<ReferenceModelClause> getReferenceModelClauses() {
        return referenceModelClauses;
    }

    public List<CellReferenceOption> getCellReferenceOptions() {
        return cellReferenceOptions;
    }

    public static enum CellReferenceOption {
        IgnoreNav("IGNORE NAV"), KeepNav("KEEP NAV"), UniqueDimension("UNIQUE DIMENSION"),
        UniqueSingleReference("UNIQUE SINGLE REFERENCE")

        ;

        public final String name;

        CellReferenceOption(){
            this(null);
        }

        CellReferenceOption(String name){
            this.name = name;
        }

        public String toString() {
            return name;
        }

    }

    public static class ReturnRowsClause extends OracleSQLObjectImpl {

        private boolean all = false;

        public boolean isAll() {
            return all;
        }

        public void setAll(boolean all) {
            this.all = all;
        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            visitor.visit(this);
            visitor.endVisit(this);
        }
    }

    public static class ReferenceModelClause extends OracleSQLObjectImpl {

        private SQLExpr                         name;
        private SQLSelect                       subQuery;
        private final List<CellReferenceOption> cellReferenceOptions = new ArrayList<CellReferenceOption>();

        public List<CellReferenceOption> getCellReferenceOptions() {
            return cellReferenceOptions;
        }

        public SQLExpr getName() {
            return name;
        }

        public void setName(SQLExpr name) {
            this.name = name;
        }

        public SQLSelect getSubQuery() {
            return subQuery;
        }

        public void setSubQuery(SQLSelect subQuery) {
            this.subQuery = subQuery;
        }

        @Override
        public void accept0(OracleASTVisitor visitor) {

        }
    }

    public static class ModelColumnClause extends OracleSQLObjectImpl {

        private QueryPartitionClause    queryPartitionClause;
        private String                  alias;
        private final List<ModelColumn> dimensionByColumns = new ArrayList<ModelColumn>();
        private final List<ModelColumn> measuresColumns    = new ArrayList<ModelColumn>();

        public List<ModelColumn> getDimensionByColumns() {
            return dimensionByColumns;
        }

        public List<ModelColumn> getMeasuresColumns() {
            return measuresColumns;
        }

        public QueryPartitionClause getQueryPartitionClause() {
            return queryPartitionClause;
        }

        public void setQueryPartitionClause(QueryPartitionClause queryPartitionClause) {
            this.queryPartitionClause = queryPartitionClause;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, queryPartitionClause);
                acceptChild(visitor, dimensionByColumns);
                acceptChild(visitor, measuresColumns);
            }
            visitor.endVisit(this);
        }

    }

    public static class ModelColumn extends OracleSQLObjectImpl {

        private SQLExpr expr;
        private String  alias;

        public SQLExpr getExpr() {
            return expr;
        }

        public void setExpr(SQLExpr expr) {
            this.expr = expr;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, expr);
            }
            visitor.endVisit(this);
        }

    }

    public static class QueryPartitionClause extends OracleSQLObjectImpl {

        private List<SQLExpr> exprList = new ArrayList<SQLExpr>();

        public List<SQLExpr> getExprList() {
            return exprList;
        }

        public void setExprList(List<SQLExpr> exprList) {
            this.exprList = exprList;
        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, exprList);
            }
        }

    }

    public static class MainModelClause extends OracleSQLObjectImpl {

        private SQLExpr                         mainModelName;
        private ModelColumnClause               modelColumnClause;

        private final List<CellReferenceOption> cellReferenceOptions = new ArrayList<CellReferenceOption>();
        private ModelRulesClause                modelRulesClause;

        public ModelRulesClause getModelRulesClause() {
            return modelRulesClause;
        }

        public void setModelRulesClause(ModelRulesClause modelRulesClause) {
            this.modelRulesClause = modelRulesClause;
        }

        public List<CellReferenceOption> getCellReferenceOptions() {
            return cellReferenceOptions;
        }

        public ModelColumnClause getModelColumnClause() {
            return modelColumnClause;
        }

        public void setModelColumnClause(ModelColumnClause modelColumnClause) {
            this.modelColumnClause = modelColumnClause;
        }

        public SQLExpr getMainModelName() {
            return mainModelName;
        }

        public void setMainModelName(SQLExpr mainModelName) {
            this.mainModelName = mainModelName;
        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, mainModelName);
                acceptChild(visitor, modelColumnClause);
                acceptChild(visitor, modelRulesClause);
            }
            visitor.endVisit(this);
        }

    }

    public static class ModelRulesClause extends OracleSQLObjectImpl {

        private final List<ModelRuleOption>    options             = new ArrayList<ModelRuleOption>();
        private SQLExpr                        iterate;
        private SQLExpr                        until;
        private final List<CellAssignmentItem> cellAssignmentItems = new ArrayList<CellAssignmentItem>();

        public SQLExpr getUntil() {
            return until;
        }

        public void setUntil(SQLExpr until) {
            this.until = until;
        }

        public SQLExpr getIterate() {
            return iterate;
        }

        public void setIterate(SQLExpr iterate) {
            this.iterate = iterate;
        }

        public List<ModelRuleOption> getOptions() {
            return options;
        }

        public List<CellAssignmentItem> getCellAssignmentItems() {
            return cellAssignmentItems;
        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, iterate);
                acceptChild(visitor, until);
                acceptChild(visitor, cellAssignmentItems);
            }
            visitor.endVisit(this);
        }

    }

    public static enum ModelRuleOption {
        UPSERT("UPSERT"), UPDATE("UPDATE"), AUTOMATIC_ORDER("AUTOMATIC ORDER"), SEQUENTIAL_ORDER("SEQUENTIAL ORDER"),

        ;

        public final String name;

        ModelRuleOption(String name){
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    public static class CellAssignmentItem extends OracleSQLObjectImpl {

        private ModelRuleOption option;
        private CellAssignment  cellAssignment;
        private SQLOrderBy      orderBy;
        private SQLExpr         expr;

        public ModelRuleOption getOption() {
            return option;
        }

        public void setOption(ModelRuleOption option) {
            this.option = option;
        }

        public CellAssignment getCellAssignment() {
            return cellAssignment;
        }

        public void setCellAssignment(CellAssignment cellAssignment) {
            this.cellAssignment = cellAssignment;
        }

        public SQLOrderBy getOrderBy() {
            return orderBy;
        }

        public void setOrderBy(SQLOrderBy orderBy) {
            this.orderBy = orderBy;
        }

        public SQLExpr getExpr() {
            return expr;
        }

        public void setExpr(SQLExpr expr) {
            this.expr = expr;
        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, cellAssignment);
                acceptChild(visitor, orderBy);
                acceptChild(visitor, expr);
            }
            visitor.endVisit(this);
        }

    }

    public static class CellAssignment extends SQLExprImpl implements OracleExpr {

        private SQLExpr             measureColumn;
        private final List<SQLExpr> conditions = new ArrayList<SQLExpr>();

        public List<SQLExpr> getConditions() {
            return conditions;
        }

        public SQLExpr getMeasureColumn() {
            return measureColumn;
        }

        public void setMeasureColumn(SQLExpr e) {
            if (e != null) {
                e.setParent(this);
            }
            this.measureColumn = e;
        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, measureColumn);
                acceptChild(visitor, conditions);
            }
            visitor.endVisit(this);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CellAssignment that = (CellAssignment) o;

            if (measureColumn != null ? !measureColumn.equals(that.measureColumn) : that.measureColumn != null)
                return false;
            return conditions != null ? conditions.equals(that.conditions) : that.conditions == null;
        }

        @Override
        public int hashCode() {
            int result = measureColumn != null ? measureColumn.hashCode() : 0;
            result = 31 * result + (conditions != null ? conditions.hashCode() : 0);
            return result;
        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            accept0((OracleASTVisitor) visitor);
        }

        @Override
        public SQLExpr clone() {
            CellAssignment x = new CellAssignment();
            if (measureColumn != null) {
                x.setMeasureColumn(measureColumn.clone());
            }
            return null;
        }

        @Override
        public List<SQLObject> getChildren() {
            List children = new ArrayList();
            children.add(measureColumn);
            children.addAll(conditions);
            return children;
        }
    }

    public ModelClause clone() {
        throw new UnsupportedOperationException();
    }
}
