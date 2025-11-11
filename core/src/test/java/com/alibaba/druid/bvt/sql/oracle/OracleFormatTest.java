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
package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;

public class OracleFormatTest extends TestCase {
    public void test_formatOracle() {
        String sql = SQLUtils.formatOracle("select substr('123''''a''''bc',0,3) FROM dual");
        System.out.println(sql);
    }
    
    public void test_format_tableAlias_without_as() {
        String sql = "select * from AB01 a where a.AAB999 = '430521463000'";
        String formattedSql = SQLUtils.format(sql, DbType.oracle);
        
        assertFalse("Table alias should not use AS keyword in Oracle", formattedSql.contains(" AS a"));
        assertTrue("Table alias should be present", formattedSql.contains("AB01 a"));
        
        System.out.println("Original SQL: " + sql);
        System.out.println("Formatted SQL: " + formattedSql);
        
        String sql2 = "SELECT t1.id, t2.name FROM users t1 JOIN orders t2 ON t1.id = t2.user_id";
        String formatted2 = SQLUtils.format(sql2, DbType.oracle);
        assertFalse("Table alias should not use AS keyword", formatted2.contains(" AS t1") || formatted2.contains(" AS t2"));
        
        String sql3 = "SELECT * FROM table1 t1, table2 t2 WHERE t1.id = t2.ref_id";
        String formatted3 = SQLUtils.format(sql3, DbType.oracle);
        assertFalse("Multiple table aliases should not use AS keyword", 
                   formatted3.contains(" AS t1") || formatted3.contains(" AS t2"));
        
        System.out.println("Test sql2: " + formatted2);
        System.out.println("Test sql3: " + formatted3);
    }
    
    public void test_format_columnAlias_with_as() {
        String sql = "SELECT name AS full_name, age FROM users u";
        String formattedSql = SQLUtils.format(sql, DbType.oracle);
        
        assertTrue("Column alias should use AS keyword", formattedSql.contains(" AS "));

        assertFalse("Table alias should not use AS keyword", formattedSql.contains("users AS u"));
        
        System.out.println("Column alias test: " + formattedSql);
    }
}
