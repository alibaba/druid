package com.alibaba.druid.bvt.sql.polardbx;

import com.alibaba.druid.sql.repository.SchemaObject;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;

public class PolarDBXTest extends TestCase {
    public void test_polardb_x_1() throws Throwable {
        SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);
        String sql1 = "CREATE TABLE `test1` (\n"
                + "  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,\n"
                + "  `serialNo` varchar(64) CHARACTER SET utf8mb4 NOT NULL DEFAULT '',\n"
                + "  `user_id` int(11) DEFAULT NULL COMMENT '用户id',\n"
                + "  PRIMARY KEY (`id`)\n"
                + ") ENGINE = InnoDB  PARTITION BY KEY(`tenant_id`,`id`)\n"
                + "PARTITIONS 21 tablegroup = `tg_p_msg`";
        repository.console(sql1);
//        repository.setDefaultSchema("test1");
        SchemaObject table = repository.findTable("test1");
        Assert.assertTrue(table != null);
        System.out.println(table.getStatement());
    }

    public void test_polardb_x_2() throws Throwable {
        SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);
        String sql2 = "CREATE TABLE `test2` (\n"
                + "  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,\n"
                + "  `serialNo` varchar(64) CHARACTER SET utf8mb4 NOT NULL DEFAULT '',\n"
                + "  `user_id` int(11) DEFAULT NULL COMMENT '用户id',\n" + "  PRIMARY KEY (`id`)\n"
                + ") ENGINE = InnoDB single";
        repository.console(sql2);
//        repository.setDefaultSchema("test2");
        SchemaObject table = repository.findTable("test2");
        Assert.assertTrue(table != null);
        System.out.println(table.getStatement());
    }

    public void test_polardb_x_3() throws Throwable {
        SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);
        String sql3 = "CREATE TABLE `test3` (\n"
                + "  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,\n"
                + "  `serialNo` varchar(64) CHARACTER SET utf8mb4 NOT NULL DEFAULT '',\n"
                + "  `user_id` int(11) DEFAULT NULL COMMENT '用户id',\n" + "  PRIMARY KEY (`id`)\n"
                + ") ENGINE = InnoDB locality = 'dn=polardbx-ng28-dn-1,polardbx-ng28-dn-2'";
        repository.console(sql3);
//        repository.setDefaultSchema("test3");
        SchemaObject table = repository.findTable("test3");
        Assert.assertTrue(table != null);
        System.out.println(table.getStatement());
    }

    public void test_polardb_x_4() throws Throwable {
        SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);
        String sql4 = "CREATE TABLE test4(\n"
                + " order_id int AUTO_INCREMENT primary key,\n"
                + " customer_id int,\n"
                + " country varchar(64),\n"
                + " city varchar(64),\n"
                + " order_time datetime not null)\n"
                + "PARTITION BY LIST COLUMNS(country,city)\n"
                + "(\n"
                + "  PARTITION p1 VALUES IN (('China','Shanghai')) LOCALITY = 'dn=polardbx-ng28-dn-2',\n"
                + "  PARTITION p2 VALUES IN (('China','Beijing')) LOCALITY = 'dn=polardbx-ng28-dn-2',\n"
                + "  PARTITION p3 VALUES IN (('China','Hangzhou')) ,\n"
                + "  PARTITION p4 VALUES IN (('China','Nanjing')) ,\n"
                + "  PARTITION p5 VALUES IN (('China','Guangzhou')) ,\n"
                + "  PARTITION p6 VALUES IN (('China','Shenzhen')) ,\n"
                + "  PARTITION p7 VALUES IN (('China','Wuhan')) ,\n"
                + "  PARTITION p8 VALUES IN (('America','New York'))\n"
                + ") LOCALITY = 'dn=polardbx-ng28-dn-0,polardbx-ng28-dn-1';";
        repository.console(sql4);
//        repository.setDefaultSchema("test4");
        SchemaObject table = repository.findTable("test4");
        Assert.assertTrue(table != null);
        System.out.println(table.getStatement());
    }

    public void test_polardb_x_5() throws Throwable {
        SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);
        String sql5 = " CREATE TABLE `test5` (\n"
            + "        `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '',\n"
            + "        `dksl` varchar(36) NOT NULL DEFAULT '' COMMENT '',\n"
            + "        `dlsc` varchar(36) NOT NULL DEFAULT '' COMMENT '',\n"
            + "        `chw` smallint(6) NOT NULL DEFAULT '0' COMMENT '',\n"
            + "        `co2o` varchar(5000) NOT NULL DEFAULT '' COMMENT '',\n"
            + "        `cnx` varchar(200) NOT NULL DEFAULT '' COMMENT '',\n"
            + "        `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',\n"
            + "        `dow` varchar(36) NOT NULL DEFAULT '' COMMENT '',\n"
            + "        PRIMARY KEY USING BTREE (`id`, `create_time`),\n"
            + "        LOCAL KEY `_local_idx_xdfd` USING BTREE (`dksl`) COMMENT '',\n"
            + "        LOCAL KEY `_local_idx_kdfs` USING BTREE (`create_time`) COMMENT ''\n"
            + ") ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 ROW_FORMAT = COMPACT COMMENT ''\n"
            + "LOCAL PARTITION BY RANGE (create_time)\n"
            + "STARTWITH '2022-01-01'\n"
            + "INTERVAL 1 MONTH\n"
            + "EXPIRE AFTER 12\n"
            + "PRE ALLOCATE 3\n"
            + "PIVOTDATE NOW()\n"
            + "DISABLE SCHEDULE\u0000";
        repository.console(sql5);
//        repository.setDefaultSchema("test4");
        SchemaObject table = repository.findTable("test5");
        Assert.assertTrue(table != null);
        System.out.println(table.getStatement());
    }


    public void test_polardb_x_5_1() throws Throwable {
        SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);
        String sql5 = "CREATE TABLE `test` (\t`id` varchar(32) NOT NULL DEFAULT '' COMMENT 'id',\n"
            + "\tKEY `idx_createTime` USING BTREE (`create_time`)\n"
            + ") ENGINE = InnoDB DEFAULT CHARSET = utf8\n"
            + "SINGLE\n"
            + "LOCAL PARTITION BY RANGE (create_time)\n"
            + "INTERVAL 1 MONTH\n"
            + "EXPIRE AFTER 27\n"
            + "PRE ALLOCATE 2\n"
            + "PIVOTDATE NOW()";
        repository.console(sql5);
//        repository.setDefaultSchema("test4");
        SchemaObject table = repository.findTable("test");
        Assert.assertTrue(table != null);
        System.out.println(table.getStatement());
    }

    public void test_polardb_x_6(){
        // test for global index with partition by
        SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);
        String sql6 = "CREATE TABLE `test6` (\n"
            + "\t`Id` varchar(32) NOT NULL COMMENT '',\n"
            + "\t`ExitId` varchar(32) NOT NULL COMMENT '',\n"
            + "\t`CreateTime` datetime NOT NULL COMMENT '创建时间',\n"
            + "\t`archive_date` datetime NOT NULL DEFAULT '2099-01-01 00:00:00',\n"
            + "\tPRIMARY KEY (`Id`, `archive_date`),\n"
            + "\tGLOBAL INDEX `g_i_id` (`id`) COVERING (`ExitId`) \n"
            + "\t\tPARTITION BY KEY(`Id`)\n"
            + "\t\tPARTITIONS 16,\n"
            + "\tKEY `ExitId` USING BTREE (`ExitId`),\n"
            + "\tKEY `CreateTime` (`CreateTime`),\n"
            + "\tKEY `i_id_ExitId` USING BTREE (`Id`, `ExitId`),\n"
            + "\tKEY `auto_shard_key_ExitId_id` USING BTREE (`ExitId`, `Id`)\n"
            + ") ENGINE = InnoDB DEFAULT CHARSET = utf8\n"
            + "PARTITION BY KEY(`ExitId`,`Id`)\n"
            + "PARTITIONS 16\n"
            + "LOCAL PARTITION BY RANGE (archive_date)\n"
            + "INTERVAL 1 MONTH\n"
            + "EXPIRE AFTER 27\n"
            + "PRE ALLOCATE 2\n"
            + "PIVOTDATE NOW()";
        repository.console(sql6);
        SchemaObject table = repository.findTable("test6");
        Assert.assertNotNull(table);
        System.out.println(table.getStatement());
        Assert.assertEquals("CREATE TABLE test6 (\n"
            + "\tId varchar(32) NOT NULL COMMENT '',\n"
            + "\tExitId varchar(32) NOT NULL COMMENT '',\n"
            + "\tCreateTime datetime NOT NULL COMMENT '创建时间',\n"
            + "\tarchive_date datetime NOT NULL DEFAULT '2099-01-01 00:00:00',\n"
            + "\tPRIMARY KEY (Id, archive_date),\n"
            + "\tGLOBAL INDEX g_i_id(id) COVERING (ExitId) PARTITION BY KEY (Id) PARTITIONS 16,\n"
            + "\tKEY ExitId USING BTREE (ExitId),\n"
            + "\tKEY CreateTime (CreateTime),\n"
            + "\tKEY i_id_ExitId USING BTREE (Id, ExitId),\n"
            + "\tKEY auto_shard_key_ExitId_id USING BTREE (ExitId, Id)\n"
            + ") ENGINE = InnoDB CHARSET = utf8\n"
            + "PARTITION BY KEY (ExitId, Id) PARTITIONS 16",table.getStatement().toString());
    }
}
