package com.alibaba.druid.bvt.bug;

import java.util.List;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;

public class Bug_for_ruiyi extends TestCase {

    public void test_for_issue() throws Exception {
        String sql = "insert into icshall_guide(id,gmt_create,gmt_modified,subject,content,cat_id)"
                     + "values (8,now(),now(),\"Why my payment is deducted incorrectly?/ Why my payment is deducted twice?\","
                     + "\"{\\\"id\\\":1,\\\"title\\\":\\\"Have you contacted your card issuer to double check instead of only checking online?\\\","
                     + "\\\"type\\\":\\\"START\\\","
                     + "\\\"currentLevel\\\":1,"
                     + "\\\"name\\\":\\\"name1\\\","
                     + "\\\"values\\\":[{\\\"id\\\":2,"
                     + "\\\"title\\\":\\\"Yes\\\","
                     + "\\\"type\\\":\\\"MIDWAY\\\","
                     + "\\\"currentLevel\\\":2,"
                     + "\\\"value\\\":1,"
                     + "\\\"childs\\\":[{\\\"id\\\":3,"
                     + "\\\"title\\\":\\\"If it is deducted twice, please contact the online service with the official bank statement.\\\","
                     + "\\\"type\\\":\\\"END\\\"," + "\\\"currentLevel\\\":3}]}," + "{\\\"id\\\":4,"
                     + "\\\"title\\\":\\\"No\\\"," + "\\\"type\\\":\\\"MIDWAY\\\"," + "\\\"currentLevel\\\":2,"
                     + "\\\"value\\\":1," + "\\\"childs\\\":[{\\\"id\\\":5,"
                     + "\\\"title\\\":\\\"Please contact your card issuer to double confirm.\\\","
                     + "\\\"type\\\":\\\"END\\\"," + "\\\"currentLevel\\\":3}]}]}\",607)";

        String expected = "INSERT INTO icshall_guide (id, gmt_create, gmt_modified, subject, content\n" +
                "\t, cat_id)\n" +
                "VALUES (8, now(), now(), 'Why my payment is deducted incorrectly?/ Why my payment is deducted twice?', '{\"id\":1,\"title\":\"Have you contacted your card issuer to double check instead of only checking online?\",\"type\":\"START\",\"currentLevel\":1,\"name\":\"name1\",\"values\":[{\"id\":2,\"title\":\"Yes\",\"type\":\"MIDWAY\",\"currentLevel\":2,\"value\":1,\"childs\":[{\"id\":3,\"title\":\"If it is deducted twice, please contact the online service with the official bank statement.\",\"type\":\"END\",\"currentLevel\":3}]},{\"id\":4,\"title\":\"No\",\"type\":\"MIDWAY\",\"currentLevel\":2,\"value\":1,\"childs\":[{\"id\":5,\"title\":\"Please contact your card issuer to double confirm.\",\"type\":\"END\",\"currentLevel\":3}]}]}'\n" +
                "\t, 607);\n";

        StringBuilder out = new StringBuilder();
        MySqlOutputVisitor visitor = new MySqlOutputVisitor(out);
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        for (SQLStatement statement : statementList) {
            statement.accept(visitor);
            visitor.print(";");
            visitor.println();
        }

        //System.out.println(out.toString());

        Assert.assertEquals(expected, out.toString());
    }
}
