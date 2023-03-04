package com.alibaba.druid.bvt.sql.mysql;

import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.util.JdbcUtils;

public class MySqlRepTest {

    public static void main(String[] args) {
        SchemaRepository repository = new SchemaRepository(JdbcUtils.MYSQL);
        repository.setDefaultSchema("test_db");
        repository.console("CREATE TABLE `test_table` (" +
                "  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT," +
                "  `varchar_column` varchar(64) DEFAULT ''," +
                "  PRIMARY KEY (`id`))");
        try {
            repository.console("CREATE INDEX idx_unikey ON test_db.test_table(varchar_column)");
            String ddl2 = "drop index idx_ordinary on test_table";
            repository.console(ddl2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
