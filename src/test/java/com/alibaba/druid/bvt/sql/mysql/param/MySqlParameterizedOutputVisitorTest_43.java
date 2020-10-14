package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_43 extends TestCase {
    public void test_for_parameterize() throws Exception {
        final DbType dbType = JdbcConstants.MYSQL;
        String sql = "UPDATE `feel_07`.feed_item_receive SET `attributes` = ?, `gmt_modified` = ?, `lock_version` = ? WHERE `feed_id` = ?";
        String params = "[\"enableTime:1498682416713,src:top,importFrom:0\",\"2017-06-29 04:40:20\",1,313825887478L]";
        String table = "[\"`feel_07`.`feed_item_receive_0502`\"]";
        String restoredSql = restore(sql,table,params);

        assertEquals("UPDATE `feel_07`.`feed_item_receive_0502`\n" +
                "SET `attributes` = 'enableTime:1498682416713,src:top,importFrom:0', `gmt_modified` = '2017-06-29 04:40:20', `lock_version` = 1\n" +
                "WHERE `feed_id` = 313825887478", restoredSql);

    }

    public static String restore(String sql, String table, String params/*JSONArray paramsArray, JSONArray destArray*/) {
        JSONArray destArray = JSON.parseArray(table.replaceAll("''", "'"));
        params = StringUtils.replace(params.replaceAll("''", "'"), "\\\"","\"");
        JSONArray paramsArray = JSON.parseArray(params);
        DbType dbType = JdbcConstants.MYSQL;
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        SQLStatement stmt = stmtList.get(0);

        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, dbType);
        List<Object> paramsList = new ArrayList<Object>(paramsArray);
        visitor.setParameters(paramsList);

        SchemaStatVisitor schemaStatVisitor = new MySqlSchemaStatVisitor();
        stmt.accept(schemaStatVisitor);
        JSONArray srcArray = new JSONArray();
        for (Map.Entry<TableStat.Name, TableStat> entry : schemaStatVisitor.getTables().entrySet()) {
            System.out.println(entry.getKey().getName());
            srcArray.add(entry.getKey().getName());
        }

        for (int i = 0; i < srcArray.size(); i++) {
            visitor.addTableMapping(srcArray.getString(i), destArray.getString(i));
        }

        stmt.accept(visitor);

        return out.toString();
    }
}
