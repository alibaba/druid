package com.alibaba.druid.sql.issues;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTriggerStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleExportParameterVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;

import junit.framework.TestCase;

public class Issue3989 extends TestCase {

    public void test_create_trigger() {

        String ddl2 = "CREATE TABLE table_1 (\n" +
            "  id INT AUTO_INCREMENT,\n" +
            "  name VARCHAR(100),\n" +
            "  PRIMARY KEY (id)\n" +
            ");\n" +
            "\n" +
            "CREATE TRIGGER my_trigger\n" +
            "    BEFORE INSERT ON table_1 \n" +
            "    FOR EACH ROW\n" +
            "BEGIN\n" +
            "    SET NEW.id = (SELECT MAX(id) + 1 FROM table_1 );\n" +
            "END;\n" +
            "\n" +
            "CREATE TABLE table_2 (\n" +
            "  id INT AUTO_INCREMENT,\n" +
            "  name VARCHAR(100),\n" +
            "  PRIMARY KEY (id)\n" +
            ");";

        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(ddl2, DbType.mysql);
        for (SQLStatement sqlStatement : sqlStatements) {
            System.out.println("解析后" + sqlStatement);
        }
        assertEquals(3, sqlStatements.size());
    }

    public void test_create_trigger2() {
        List<SQLCreateTriggerStatement> createTriggerStatementList = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        String sql = "CREATE OR REPLACE TRIGGER trig_test\n" +
            "AFTER UPDATE OF ID\n" +
            "ON test_trig_tab\n" +
            "FOR EACH ROW WHEN (OLD.ID>=10)\n" +
            "DECLARE num INTEGER;\n" +
            "BEGIN INSERT INTO test_trig_tab2 VALUES(:NEW.ID,'trig_update');\n" +
            "END;\n" +
            "\n" +
            "CREATE OR REPLACE TRIGGER trig_test3\n" +
            "AFTER INSERT OR UPDATE OF id,NANE ON test_trig_tab\n" +
            "FOR each ROW BEGIN\n" +
            "IF updating THEN INSERT INTO test_trig_tab2 VALUES(:NEW.id,'update tab');\n" +
            "END IF;\n" +
            "IF inserting THEN\n" +
            "INSERT INTO test_trig_tab2 VALUES(:NEW.id,'hh');\n" +
            "END IF;\n" +
            "END;";

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();

        final StringBuilder out = new StringBuilder();
        final OracleExportParameterVisitor visitor1 = new OracleExportParameterVisitor(out);
        visitor1.setParameterizedMergeInList(true);

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        System.out.println("Trigger statement size: " + statementList.size());
        for (SQLStatement statement : statementList) {
            statement.accept(visitor1);
            statement.accept(visitor);
            SQLCreateTriggerStatement sqlCreateTriggerStatement = (SQLCreateTriggerStatement) statement;
            createTriggerStatementList.add(sqlCreateTriggerStatement);
        }
        System.out.println("统计SQL中使用的表、字段、过滤条件、排序表达式、分组表达式");
        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        //System.out.println("Parameters : " + visitor.getParameters());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());
        System.out.println("提取SQL中的变量参数");
        assertEquals(2, createTriggerStatementList.size());
        for (SQLCreateTriggerStatement sqlCreateTriggerStatement : createTriggerStatementList) {
            System.out.println("触发器名: " + sqlCreateTriggerStatement.getName());
            System.out.println("触发器body: " + sqlCreateTriggerStatement.getBody());
            System.out.println("作用的表: " + sqlCreateTriggerStatement.getOn());
            System.out.println("触发事件: " + sqlCreateTriggerStatement.getTriggerEvents());
            System.out.println("触发时机: " + sqlCreateTriggerStatement.getTriggerType());
            System.out.println("更新的列: " + sqlCreateTriggerStatement.getUpdateOfColumns());
            System.out.println("条件: " + sqlCreateTriggerStatement.getWhen());
        }
    }
}
