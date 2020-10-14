package com.alibaba.druid.sql.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.sql.ast.SQLStructDataType.Field;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SQLRowDataType extends SQLObjectImpl implements SQLDataType {
    private DbType dbType;
    private List<Field> fields = new ArrayList<Field>();

    public SQLRowDataType() {

    }

    public SQLRowDataType(DbType dbType) {
        this.dbType = dbType;
    }

    @Override
    public String getName() {
        return "ROW";
    }

    @Override
    public long nameHashCode64() {
        return FnvHash.Constants.ROW;
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SQLExpr> getArguments() {
        return Collections.emptyList();
    }

    @Override
    public Boolean getWithTimeZone() {
        return null;
    }

    @Override
    public void setWithTimeZone(Boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWithLocalTimeZone() {
        return false;
    }

    @Override
    public void setWithLocalTimeZone(boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    @Override
    public DbType getDbType() {
        return dbType;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, fields);
        }
        visitor.endVisit(this);
    }

    public SQLRowDataType clone() {
        SQLRowDataType x = new SQLRowDataType(dbType);

        for (Field field : fields) {
            x.addField(field.getName(), field.getDataType().clone());
        }

        return x;
    }

    public List<Field> getFields() {
        return fields;
    }

    public Field addField(SQLName name, SQLDataType dataType) {
        Field field = new Field(name, dataType);
        field.setParent(this);
        fields.add(field);
        return field;
    }

    public int jdbcType() {
        return Types.STRUCT;
    }

    @Override
    public boolean isInt() {
        return false;
    }

    @Override
    public boolean isNumberic() {
        return false;
    }

    @Override
    public boolean isString() {
        return false;
    }

    @Override
    public boolean hasKeyLength() {
        return false;
    }
}
