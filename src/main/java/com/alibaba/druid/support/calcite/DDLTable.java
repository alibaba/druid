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
package com.alibaba.druid.support.calcite;

import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.rel.type.*;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.Statistic;
import org.apache.calcite.schema.Table;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.type.SqlTypeName;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenshao on 17/07/2017.
 */
public class DDLTable implements Table, RelDataType {
    private SQLCreateTableStatement stmt;
    private List<String> fieldNames = new ArrayList<String>();
    private List<SQLColumnDefinition> columns = new ArrayList<SQLColumnDefinition>();
    private List<RelDataTypeField> fields = new ArrayList<RelDataTypeField>();

    public DDLTable(SQLCreateTableStatement stmt) {
        this.stmt = stmt;

        for (SQLTableElement e : stmt.getTableElementList()) {
            if (e instanceof SQLColumnDefinition) {
                SQLColumnDefinition column = (SQLColumnDefinition) e;
                String fieldName = column.getName().getSimpleName();
                fieldNames.add(fieldName);
                columns.add(column);
                DDLColumn field = new DDLColumn(this, column, fields.size());
                this.fields.add(field);
            }
        }
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        return this;
    }

    @Override
    public Statistic getStatistic() {
        return null;
    }

    @Override
    public Schema.TableType getJdbcTableType() {
        return Schema.TableType.TABLE;
    }

    @Override
    public boolean isRolledUp(String column) {
        return false;
    }

    @Override
    public boolean rolledUpColumnValidInsideAgg(String column, SqlCall call, SqlNode parent, CalciteConnectionConfig config) {
        return false;
    }


    @Override
    public boolean isStruct() {
        return false;
    }

    @Override
    public List<RelDataTypeField> getFieldList() {
        return fields;
    }

    @Override
    public List<String> getFieldNames() {
        return fieldNames;
    }

    @Override
    public int getFieldCount() {
        return fieldNames.size();
    }

    @Override
    public StructKind getStructKind() {
        return null;
    }

    @Override
    public RelDataTypeField getField(String fieldName, boolean caseSensitive, boolean elideRecord) {
        return null;
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public RelDataType getComponentType() {
        return null;
    }

    @Override
    public RelDataType getKeyType() {
        return null;
    }

    @Override
    public RelDataType getValueType() {
        return null;
    }

    @Override
    public Charset getCharset() {
        return null;
    }

    @Override
    public SqlCollation getCollation() {
        return null;
    }

    @Override
    public SqlIntervalQualifier getIntervalQualifier() {
        return null;
    }

    @Override
    public int getPrecision() {
        return 0;
    }

    @Override
    public int getScale() {
        return 0;
    }

    @Override
    public SqlTypeName getSqlTypeName() {
        return null;
    }

    @Override
    public SqlIdentifier getSqlIdentifier() {
        return null;
    }

    @Override
    public String getFullTypeString() {
        return null;
    }

    @Override
    public RelDataTypeFamily getFamily() {
        return null;
    }

    @Override
    public RelDataTypePrecedenceList getPrecedenceList() {
        return null;
    }

    @Override
    public RelDataTypeComparability getComparability() {
        return null;
    }

    @Override
    public boolean isDynamicStruct() {
        return false;
    }

}
