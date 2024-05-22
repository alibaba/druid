package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SQLParameterTest {

    @Test
    public void testDefaultConstructor() {
        SQLParameter parameter = new SQLParameter();

        assertNull(parameter.getName());
        assertNull(parameter.getDataType());
        assertNull(parameter.getDefaultValue());
    }

    @Test
    public void testConstructorWithNameAndDataType() {
        // DECLARE testName testType;
        SQLName name = new SQLIdentifierExpr("testName");
        SQLDataType dataType = new SQLDataTypeImpl("testType");
        SQLParameter sqlParameter = new SQLParameter(name, dataType);

        assertEquals(name, sqlParameter.getName());
        assertEquals(dataType, sqlParameter.getDataType());
        assertNull(sqlParameter.getDefaultValue());
        
        // Test parent relationship
        assertEquals(sqlParameter, name.getParent());
        assertEquals(sqlParameter, dataType.getParent());
    }

    @Test
    public void testConstructorWithNameDataTypeAndDefaultValue() {
        // DECLARE testName testType;
        SQLName name = new SQLIdentifierExpr("testName");
        SQLDataType dataType = new SQLDataTypeImpl("testType");
        SQLExpr defaultValue = new SQLIdentifierExpr("testDefault");
        SQLParameter sqlParameter = new SQLParameter(name, dataType, defaultValue);

        assertEquals(name, sqlParameter.getName());
        assertEquals(dataType, sqlParameter.getDataType());
        assertEquals(defaultValue, sqlParameter.getDefaultValue());

        // Test parent relationship
        assertEquals(sqlParameter, name.getParent());
        assertEquals(sqlParameter, dataType.getParent());
        assertEquals(sqlParameter, defaultValue.getParent());
    }
}
