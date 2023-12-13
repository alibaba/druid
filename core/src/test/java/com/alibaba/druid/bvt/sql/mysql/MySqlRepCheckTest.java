package com.alibaba.druid.bvt.sql.mysql;

import com.alibaba.druid.sql.repository.Schema;
import com.alibaba.druid.sql.repository.SchemaObject;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MySqlRepCheckTest {
    final private SchemaRepository repository = new SchemaRepository(JdbcUtils.MYSQL);

    @Test
    public void test_check() {
        MySqlRepCheckTest testRep = new MySqlRepCheckTest();
        String schemaName = "test_db";
        testRep.repository.setDefaultSchema(schemaName);
        testRep.repository.console("CREATE TABLE `test_table` (\n" +
                "  `id` int NOT NULL AUTO_INCREMENT,\n" +
                "  `action_flag` smallint unsigned NOT NULL,\n" +
                "  CONSTRAINT `chk_1` CHECK ((`action_flag` >= 0))\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");
        Map<String, String> snapshot = testRep.snapshot();
        System.out.println(snapshot.get(schemaName));
    }

    public Map<String, String> snapshot() {
        Map<String, String> schemaDdls = new HashMap<String, String>();
        for (Schema schema : repository.getSchemas()) {
            StringBuilder data = new StringBuilder(4 * 1024);
            for (String table : schema.showTables()) {
                SchemaObject schemaObject = schema.findTable(table);
                schemaObject.getStatement().output(data);
                data.append("; \n");
            }
            schemaDdls.put(schema.getName(), data.toString());
        }

        return schemaDdls;
    }
}