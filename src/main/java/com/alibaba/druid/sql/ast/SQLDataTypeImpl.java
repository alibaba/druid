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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.FnvHash;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class SQLDataTypeImpl extends SQLObjectImpl implements SQLDataType, SQLDbTypedObject {

    private         String        name;
    private         long          nameHashCode64;
    protected final List<SQLExpr> arguments = new ArrayList<SQLExpr>();
    private         Boolean       withTimeZone;
    private         boolean       withLocalTimeZone = false;
    private         DbType        dbType;

    private         boolean       unsigned;
    private         boolean       zerofill;

    // for oracle
    private         SQLExpr       indexBy;

    public SQLDataTypeImpl(){

    }

    public SQLDataTypeImpl(String name){
        this.name = name;
    }

    public SQLDataTypeImpl(String name, int precision) {
        this(name);
        addArgument(new SQLIntegerExpr(precision));
    }

    public SQLDataTypeImpl(String name, SQLExpr arg) {
        this(name);
        addArgument(arg);
    }

    public SQLDataTypeImpl(String name, int precision, int scale) {
        this(name);
        addArgument(new SQLIntegerExpr(precision));
        addArgument(new SQLIntegerExpr(scale));
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            for (int i = 0; i < arguments.size(); i++) {
                SQLExpr arg = arguments.get(i);
                if (arg != null) {
                    arg.accept(visitor);
                }
            }
        }

        visitor.endVisit(this);
    }

    public String getName() {
        return this.name;
    }

    public long nameHashCode64() {
        if (nameHashCode64 == 0) {
            nameHashCode64 = FnvHash.hashCode64(name);
        }
        return nameHashCode64;
    }

    public void setName(String name) {
        this.name = name;
        nameHashCode64 = 0L;
    }

    public List<SQLExpr> getArguments() {
        return this.arguments;
    }
    
    public void addArgument(SQLExpr argument) {
        if (argument != null) {
            argument.setParent(this);
        }
        this.arguments.add(argument);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLDataTypeImpl dataType = (SQLDataTypeImpl) o;

        if (name != null ? !name.equals(dataType.name) : dataType.name != null) return false;
        if (!arguments.equals(dataType.arguments)){
            return false;
        }
        return withTimeZone != null ? withTimeZone.equals(dataType.withTimeZone) : dataType.withTimeZone == null;
    }

    @Override
    public int hashCode() {
        long value = nameHashCode64();
        return (int)(value ^ (value >>> 32));
    }

    @Override
    public Boolean getWithTimeZone() {
        return withTimeZone;
    }

    public void setWithTimeZone(Boolean withTimeZone) {
        this.withTimeZone = withTimeZone;
    }

    public boolean isWithLocalTimeZone() {
        return withLocalTimeZone;
    }

    public void setWithLocalTimeZone(boolean withLocalTimeZone) {
        this.withLocalTimeZone = withLocalTimeZone;
    }

    public DbType getDbType() {
        return dbType;
    }

    @Override
    public int jdbcType() {
        long nameNash = nameHashCode64();

        if (nameNash == FnvHash.Constants.TINYINT) {
            return Types.TINYINT;
        }

        if (nameNash == FnvHash.Constants.SMALLINT) {
            return Types.SMALLINT;
        }

        if (nameNash == FnvHash.Constants.INT || nameNash == FnvHash.Constants.INTEGER) {
            return Types.INTEGER;
        }

        if (nameNash == FnvHash.Constants.BIGINT) {
            return Types.BIGINT;
        }

        if (nameNash == FnvHash.Constants.DECIMAL) {
            return Types.DECIMAL;
        }

        if (nameNash == FnvHash.Constants.FLOAT) {
            return Types.FLOAT;
        }

        if (nameNash == FnvHash.Constants.REAL) {
            return Types.REAL;
        }

        if (nameNash == FnvHash.Constants.DOUBLE) {
            return Types.DOUBLE;
        }

        if (nameNash == FnvHash.Constants.NUMBER || nameNash == FnvHash.Constants.NUMERIC) {
            return Types.NUMERIC;
        }

        if (nameNash == FnvHash.Constants.BOOLEAN) {
            return Types.BOOLEAN;
        }

        if (nameNash == FnvHash.Constants.DATE || nameNash == FnvHash.Constants.NEWDATE) {
            return Types.DATE;
        }

        if (nameNash == FnvHash.Constants.DATETIME || nameNash == FnvHash.Constants.TIMESTAMP) {
            return Types.TIMESTAMP;
        }

        if (nameNash == FnvHash.Constants.TIME) {
            return Types.TIME;
        }

        if (nameNash == FnvHash.Constants.BLOB) {
            return Types.BLOB;
        }

        if (nameNash == FnvHash.Constants.ROWID) {
            return Types.ROWID;
        }

        if (nameNash == FnvHash.Constants.REF) {
            return Types.REF;
        }

        if (nameNash == FnvHash.Constants.TINYINT || nameNash == FnvHash.Constants.TINY) {
            return Types.TINYINT;
        }

        if (nameNash == FnvHash.Constants.SMALLINT || nameNash == FnvHash.Constants.SHORT) {
            return Types.SMALLINT;
        }

        if (nameNash == FnvHash.Constants.INT
                || nameNash == FnvHash.Constants.INT24
                || nameNash == FnvHash.Constants.INTEGER) {
            return Types.INTEGER;
        }

        if (nameNash == FnvHash.Constants.NUMBER || nameNash == FnvHash.Constants.NUMERIC) {
            return Types.NUMERIC;
        }

        if (nameNash == FnvHash.Constants.BOOLEAN) {
            return Types.BOOLEAN;
        }

        if (nameNash == FnvHash.Constants.DATE
                || nameNash == FnvHash.Constants.YEAR
                || nameNash == FnvHash.Constants.NEWDATE) {
            return Types.DATE;
        }

        if (nameNash == FnvHash.Constants.DATETIME || nameNash == FnvHash.Constants.TIMESTAMP) {
            return Types.TIMESTAMP;
        }

        if (nameNash == FnvHash.Constants.TIME) {
            return Types.TIME;
        }

        if (nameNash == FnvHash.Constants.TINYBLOB) {
            return Types.VARBINARY;
        }

        if (nameNash == FnvHash.Constants.BLOB) {
            return Types.BLOB;
        }

        if (nameNash == FnvHash.Constants.LONGBLOB) {
            return Types.LONGVARBINARY;
        }

        if (nameNash == FnvHash.Constants.ROWID) {
            return Types.ROWID;
        }

        if (nameNash == FnvHash.Constants.REF) {
            return Types.REF;
        }

        if (nameNash == FnvHash.Constants.BINARY || nameNash == FnvHash.Constants.GEOMETRY) {
            return Types.BINARY;
        }

        if (nameNash == FnvHash.Constants.SQLXML) {
            return Types.SQLXML;
        }

        if (nameNash == FnvHash.Constants.BIT) {
            return Types.BIT;
        }

        if (nameNash == FnvHash.Constants.NCHAR) {
            return Types.NCHAR;
        }

        if (nameNash == FnvHash.Constants.CHAR
                || nameNash == FnvHash.Constants.ENUM
                || nameNash == FnvHash.Constants.SET
                || nameNash == FnvHash.Constants.JSON) {
            return Types.CHAR;
        }

        if (nameNash == FnvHash.Constants.VARCHAR
                || nameNash == FnvHash.Constants.VARCHAR2
                || nameNash == FnvHash.Constants.STRING) {
            return Types.VARCHAR;
        }

        if (nameNash == FnvHash.Constants.NVARCHAR || nameNash == FnvHash.Constants.NVARCHAR2) {
            return Types.NVARCHAR;
        }

        if (nameNash == FnvHash.Constants.CLOB
                || nameNash == FnvHash.Constants.TEXT
                || nameNash == FnvHash.Constants.TINYTEXT
                || nameNash == FnvHash.Constants.MEDIUMTEXT
                || nameNash == FnvHash.Constants.LONGTEXT) {
            return Types.CLOB;
        }

        if (nameNash == FnvHash.Constants.NCLOB) {
            return Types.NCLOB;
        }


        if (nameNash == FnvHash.Constants.TINYBLOB) {
            return Types.VARBINARY;
        }

        if (nameNash == FnvHash.Constants.LONGBLOB) {
            return Types.LONGVARBINARY;
        }

        if (nameNash == FnvHash.Constants.BINARY || nameNash == FnvHash.Constants.GEOMETRY) {
            return Types.BINARY;
        }

        if (nameNash == FnvHash.Constants.SQLXML) {
            return Types.SQLXML;
        }

        //

        if (nameNash == FnvHash.Constants.NCHAR) {
            return Types.NCHAR;
        }

        if (nameNash == FnvHash.Constants.CHAR || nameNash == FnvHash.Constants.JSON) {
            return Types.CHAR;
        }

        if (nameNash == FnvHash.Constants.VARCHAR
                || nameNash == FnvHash.Constants.VARCHAR2
                || nameNash == FnvHash.Constants.STRING) {
            return Types.VARCHAR;
        }

        if (nameNash == FnvHash.Constants.NVARCHAR || nameNash == FnvHash.Constants.NVARCHAR2) {
            return Types.NVARCHAR;
        }

        if (nameNash == FnvHash.Constants.CLOB
                || nameNash == FnvHash.Constants.TEXT
                || nameNash == FnvHash.Constants.TINYTEXT
                || nameNash == FnvHash.Constants.MEDIUMTEXT
                || nameNash == FnvHash.Constants.LONGTEXT) {
            return Types.CLOB;
        }

        if (nameNash == FnvHash.Constants.NCLOB) {
            return Types.NCLOB;
        }

        return 0;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    public SQLDataTypeImpl clone() {
        SQLDataTypeImpl x = new SQLDataTypeImpl();

        cloneTo(x);

        return x;
    }

    public void cloneTo(SQLDataTypeImpl x) {
        x.dbType = dbType;
        x.name = name;
        x.nameHashCode64 = nameHashCode64;

        for (SQLExpr arg : arguments) {
            x.addArgument(arg.clone());
        }

        x.withTimeZone = withTimeZone;
        x.withLocalTimeZone = withLocalTimeZone;
        x.zerofill = zerofill;
        x.unsigned = unsigned;

        if (indexBy != null) {
            x.setIndexBy(indexBy.clone());
        }
    }

    public String toString() {
        return SQLUtils.toSQLString(this, dbType);
    }

    public boolean isUnsigned() {
        return unsigned;
    }

    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }

    public boolean isZerofill() {
        return zerofill;
    }

    public void setZerofill(boolean zerofill) {
        this.zerofill = zerofill;
    }

    public SQLExpr getIndexBy() {
        return indexBy;
    }

    public void setIndexBy(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.indexBy = x;
    }

    public boolean isInt() {
        long hashCode64 = nameHashCode64();

        return hashCode64 == FnvHash.Constants.BIGINT
                || hashCode64 == FnvHash.Constants.INT
                || hashCode64 == FnvHash.Constants.INT4
                || hashCode64 == FnvHash.Constants.INT24
                || hashCode64 == FnvHash.Constants.SMALLINT
                || hashCode64 == FnvHash.Constants.TINYINT
                || hashCode64 == FnvHash.Constants.INTEGER;
    }

    public boolean isNumberic() {
        long hashCode64 = nameHashCode64();

        return hashCode64 == FnvHash.Constants.REAL
                || hashCode64 == FnvHash.Constants.FLOAT
                || hashCode64 == FnvHash.Constants.DOUBLE
                || hashCode64 == FnvHash.Constants.DOUBLE_PRECISION
                || hashCode64 == FnvHash.Constants.NUMBER
                || hashCode64 == FnvHash.Constants.DECIMAL;
    }

    public boolean isString() {
        long hashCode64 = nameHashCode64();

        return hashCode64 == FnvHash.Constants.VARCHAR
                || hashCode64 == FnvHash.Constants.VARCHAR2
                || hashCode64 == FnvHash.Constants.CHAR
                || hashCode64 == FnvHash.Constants.NCHAR
                || hashCode64 == FnvHash.Constants.NVARCHAR
                || hashCode64 == FnvHash.Constants.NVARCHAR2
                || hashCode64 == FnvHash.Constants.TEXT
                || hashCode64 == FnvHash.Constants.TINYTEXT
                || hashCode64 == FnvHash.Constants.MEDIUMTEXT
                || hashCode64 == FnvHash.Constants.LONGTEXT
                || hashCode64 == FnvHash.Constants.CLOB
                || hashCode64 == FnvHash.Constants.NCLOB
                || hashCode64 == FnvHash.Constants.MULTIVALUE
                || hashCode64 == FnvHash.Constants.STRING;
    }

    @Override
    public boolean hasKeyLength() {
        long hashCode64 = nameHashCode64();

        return hashCode64 == FnvHash.Constants.VARCHAR
                || hashCode64 == FnvHash.Constants.VARCHAR2
                || hashCode64 == FnvHash.Constants.CHAR
                || hashCode64 == FnvHash.Constants.NCHAR
                || hashCode64 == FnvHash.Constants.NVARCHAR
                || hashCode64 == FnvHash.Constants.NVARCHAR2
                || hashCode64 == FnvHash.Constants.TEXT
                || hashCode64 == FnvHash.Constants.TINYTEXT
                || hashCode64 == FnvHash.Constants.MEDIUMTEXT
                || hashCode64 == FnvHash.Constants.LONGTEXT
                || hashCode64 == FnvHash.Constants.CLOB
                || hashCode64 == FnvHash.Constants.NCLOB
                || hashCode64 == FnvHash.Constants.MULTIVALUE
                || hashCode64 == FnvHash.Constants.STRING
                || hashCode64 == FnvHash.Constants.BLOB
                || hashCode64 == FnvHash.Constants.TINYBLOB
                || hashCode64 == FnvHash.Constants.LONGBLOB
                || hashCode64 == FnvHash.Constants.BINARY
                || hashCode64 == FnvHash.Constants.VARBINARY;
    }
}
