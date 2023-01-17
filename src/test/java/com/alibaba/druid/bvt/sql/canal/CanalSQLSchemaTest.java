package com.alibaba.druid.bvt.sql.canal;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.druid.sql.repository.SchemaObject;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.util.JdbcConstants;

public class CanalSQLSchemaTest {

    @Test
    public void testSimple() throws Throwable {
        SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);
        String sql1 = "CREATE TABLE `table_x1` ( `id` bigint(20) NOT NULL AUTO_INCREMENT, "
                      + "`key1` longtext NOT NULL COMMENT 'key1', `value1` longtext NOT NULL COMMENT 'value1', PRIMARY KEY (`id`) )"
                      + "ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        String sql2 = " CREATE TABLE IF NOT EXISTS `table_x1` ( `id` bigint(20) NOT NULL AUTO_INCREMENT,"
                      + "`key1` longtext NOT NULL COMMENT 'key1',PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        repository.console(sql1);
        repository.console(sql2);
        repository.setDefaultSchema("test");
        SchemaObject table = repository.findTable("table_x1");
        Assert.assertTrue(
                table.findColumn("value1") != null);
    }

    @Test
    public void test_block_format() throws Throwable {
        SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);
        String sql = " CREATE TABLE `parent` (`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,"
                     + "`created_at` timestamp NULL DEFAULT NULL, " + "PRIMARY KEY (`id`)"
                     + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 BLOCK_FORMAT=ENCRYPTED";
        repository.console(sql);
        repository.setDefaultSchema("test");
        SchemaObject table = repository.findTable("parent");
        Assert.assertTrue(table.findColumn("id") != null);
    }

    @Test
    public void test_json_index() throws Throwable {
        SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);
        String sql = " CREATE TABLE `articles` ( \n" +
                " \t`article_id` bigint NOT NULL AUTO_INCREMENT, \n" +
                " \t`tags` json DEFAULT NULL, PRIMARY KEY (`article_id`), \n" +
                " \tKEY `articles_tags` (\n" +
                " \t\t(\n" +
                " \t\t\tcast(json_extract(`tags`,_utf8mb4'$[*]') \n" +
                " \t\t\t\tas char(40) array)\n" +
                " \t\t)\n" +
                " \t)\n" +
                " ) ENGINE=InnoDB AUTO_INCREMENT=1054 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        repository.console(sql);
        repository.setDefaultSchema("test");
        SchemaObject table = repository.findTable("articles");
        Assert.assertTrue(table.findColumn("article_id") != null);
    }

    @Test
    public void test_invisible() throws Throwable {
        SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);
        String sql = " CREATE TABLE `proposal_order_info` (`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,"
                     + "`created_at` timestamp NULL DEFAULT NULL, " + "PRIMARY KEY (`id`) , "
                     + "KEY `idx_create_time` (`created_at`) /*!80000 INVISIBLE */"
                     + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 BLOCK_FORMAT=ENCRYPTED";
        repository.console(sql);
        repository.setDefaultSchema("test");
        SchemaObject table = repository.findTable("proposal_order_info");
        Assert.assertTrue(table.findColumn("id") != null);
    }

    @Test
    public void test_persistent() throws Throwable {
        SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);
        String sql = " create table example_vc_tbl( c1 int not null auto_increment primary key,"
                     + "c2 varchar(70), vc1 int as (length(c2)) virtual,"
                     + "DIM_SUM varchar(128) AS (MD5(UPPER(CONCAT(c2, c1)))) PERSISTENT)";
        repository.console(sql);
        repository.setDefaultSchema("test");
        SchemaObject table = repository.findTable("example_vc_tbl");
        Assert.assertTrue(table.findColumn("DIM_SUM") != null);
    }
}
