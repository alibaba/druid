package com.alibaba.druid.sql.dialect.oracle.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.Assert;
import org.junit.Test;

public class OracleParameterParserTest {
    @Test
    public void testNestedParameter() {
        String sql = "DECLARE\n" +
                "    TYPE Foursome IS TABLE OF VARCHAR2(15);\n" +
                "    team Foursome := Foursome('John', 'Mary', 'Alberto', 'Juanita');\n" +
                "BEGIN\n" +
                "    DBMS_OUTPUT.PUT_LINE('2001 Team:');\n" +
                "    FOR i IN 1..4\n" +
                "    LOOP\n" +
                "        DBMS_OUTPUT.PUT_LINE(i || '.' || team(i));\n" +
                "    END LOOP;\n" +
                "END;";
        SQLStatement stat = SQLUtils.parseSingleStatement(sql, DbType.oracle, false);
        System.out.println(stat);
        Assert.assertEquals(sql, stat.toString());
        System.out.println("=============");
    }

    @Test
    public void testNestedParameter_NumberIndexBy() {
        String sql = "DECLARE\n" +
                "    TYPE NUMBER_ARRAY_TYPE IS TABLE OF NUMBER INDEX BY BINARY_INTEGER;\n" +
                "    myArray NUMBER_ARRAY_TYPE;\n" +
                "BEGIN\n" +
                "    FOR i IN 1..10\n" +
                "    LOOP\n" +
                "        myArray(i) := i * 10;\n" +
                "    END LOOP;\n" +
                "    FOR i IN 1..10\n" +
                "    LOOP\n" +
                "        DBMS_OUTPUT.PUT_LINE('Index: ' || i || ', Value: ' || myArray(i));\n" +
                "    END LOOP;\n" +
                "END;";
        SQLStatement stat = SQLUtils.parseSingleStatement(sql, DbType.oracle, false);
        System.out.println(stat);
        Assert.assertEquals(sql, stat.toString());
        System.out.println("=============");
    }
}
