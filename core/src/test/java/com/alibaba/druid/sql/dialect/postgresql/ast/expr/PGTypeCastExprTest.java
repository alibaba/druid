package com.alibaba.druid.sql.dialect.postgresql.ast.expr;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class PGTypeCastExprTest {

    @Test
    public void testDefaultConstructor() {
        PGTypeCastExpr pgTypeCastExpr = new PGTypeCastExpr();
        assertNotNull(pgTypeCastExpr);
        assertNull(pgTypeCastExpr.getExpr());
        assertNull(pgTypeCastExpr.getDataType());
    }

    @Test
    public void testParameterizedConstructor() {
        // '100'::INT
        SQLExpr sqlCharExpr = new SQLCharExpr("100");
        SQLDataType sqlDataType = new SQLDataTypeImpl(SQLDataType.Constants.INT);

        PGTypeCastExpr pgTypeCastExpr = new PGTypeCastExpr(sqlCharExpr, sqlDataType);
        assertNotNull(pgTypeCastExpr);
        assertEquals(sqlCharExpr, pgTypeCastExpr.getExpr());
        assertEquals(sqlDataType, pgTypeCastExpr.getDataType());
        
        // Test parent relationship
        assertEquals(pgTypeCastExpr, sqlCharExpr.getParent());
        assertEquals(pgTypeCastExpr, sqlDataType.getParent());
    }
    
    @Test
    public void testClone() {
        // '100'::INT
        SQLExpr sqlCharExpr = new SQLCharExpr("100");
        SQLDataType sqlDataType = new SQLDataTypeImpl(SQLDataType.Constants.INT);

        PGTypeCastExpr pgTypeCastExpr = new PGTypeCastExpr(sqlCharExpr, sqlDataType);
        PGTypeCastExpr pgTypeCastExprClone = pgTypeCastExpr.clone();
        assertNotNull(pgTypeCastExprClone);
        assertEquals(pgTypeCastExpr, pgTypeCastExprClone);
        assertEquals(pgTypeCastExpr.getExpr(), pgTypeCastExprClone.getExpr());
        assertEquals(pgTypeCastExpr.getDataType(), pgTypeCastExprClone.getDataType());
    }
}
