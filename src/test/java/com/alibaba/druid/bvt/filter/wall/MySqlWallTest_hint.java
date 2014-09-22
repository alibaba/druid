package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

public class MySqlWallTest_hint extends TestCase {

    public void test_false() throws Exception {
        WallConfig config = new WallConfig();
        config.setHintAllow(false);
        String sql = "select * from person where id = '3'/**/union select 0,1,v from (select 1,2,user/*!() as v*/) a where '1'<>''";
        Assert.assertFalse(WallUtils.isValidateMySql(sql, config)); //
    }

    public void test_false_1() throws Exception {
        WallConfig config = new WallConfig();
        config.setHintAllow(true);
        config.setMultiStatementAllow(true);
        String sql = "select * from person where id = '3'/**/union select 0,1,v from (select 1,2,user/*!() as v*/) a where '1'<>''";
        Assert.assertFalse(WallUtils.isValidateMySql(sql, config)); //
    }
    
    public void test_true() throws Exception {
        String sql = "SELECT /*! STRAIGHT_JOIN */ col1 FROM table1,table2";
        Assert.assertTrue(WallUtils.isValidateMySql(sql)); //
    }
    
    public void test_true_1() throws Exception {
        String sql = "/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */";
        Assert.assertTrue(WallUtils.isValidateMySql(sql)); //
    }
    
    public void test_true_2() throws Exception {
        String sql = "/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */";
        Assert.assertTrue(WallUtils.isValidateMySql(sql)); //
    }
    
    public void test_true_3() throws Exception {
        WallConfig config = new WallConfig();
        config.setHintAllow(true);
        config.setMultiStatementAllow(true);
        String sql = " /*!50003 CREATE*/ /*!50020  /*!50003 PROCEDURE `top_calculate_customer_update`(in update_time DATETIME)"
                     + " DETERMINISTIC"
                     + " BEGIN"
                     + " DECLARE done INT DEFAULT FALSE;"
                     + " DECLARE c_receiver_city,c_receiver_district,c_receiver_address VARCHAR(200);"
                     + " DECLARE c_last_updated DATETIME;"
                     + " DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;"
                     + " insert into top_calculate_customer_log (execute_time,info) values (now(),'update table top_taobao_order_customer_mid begin');"
                     + " update top_tmp_order_middle_last_sync set status = 'wait', last_sync = update_time where plat_form = 'taobao' and status = 'blocking';"
                     + " END */";
        Assert.assertTrue(WallUtils.isValidateMySql(sql, config)); 
    }
    
    public void test_true_4() throws Exception {
        WallConfig config = new WallConfig();
        config.setHintAllow(true);
        config.setMultiStatementAllow(true);
        String sql = "LOCK TABLES `m_rpt_adgroupeffect` READ /*!32311 LOCAL */";
        Assert.assertTrue(WallUtils.isValidateMySql(sql, config)); //
    }
    
    public void test_true_5() throws Exception {
        WallConfig config = new WallConfig();
        config.setHintAllow(true);
        config.setMultiStatementAllow(true);
        String sql = "DROP TABLE IF EXISTS `item_similarity`;"//
                     + "\n/*!40101 SET @saved_cs_client     = @@character_set_client */;"//
                     + "\n/*!40101 SET character_set_client = utf8 */;" //
                     + "\nCREATE TABLE `item_similarity` ("//
                     + " `id` bigint(20) unsigned NOT NULL, "//
                     + " `sellerId` bigint(20) DEFAULT NULL,"//
                     + " PRIMARY KEY (`id`)" //
                     + " ) ENGINE=InnoDB DEFAULT CHARSET=utf8;"//
                     + " \n/*!40101 SET character_set_client = @saved_cs_client */;";
        Assert.assertTrue(WallUtils.isValidateMySql(sql, config)); //
    }
    
    public void test_true_6() throws Exception {
        String sql = "START TRANSACTION /*!40100 WITH CONSISTENT SNAPSHOT */";
        Assert.assertTrue(WallUtils.isValidateMySql(sql)); //
    }

    public void test_true_7() throws Exception {
        String sql = "LOCK TABLES `m_rpt_adgroupeffect` READ /*!32311 LOCAL */";
        Assert.assertTrue(WallUtils.isValidateMySql(sql)); //
    }

    public void test_true_8() throws Exception {
        String sql = "SET SQL_QUOTE_SHOW_CREATE=1/*!40102 ,SQL_MODE=concat(@@sql_mode, _utf8 ',NO_KEY_OPTIONS,NO_TABLE_OPTIONS,NO_FIELD_OPTIONS') */";
        Assert.assertTrue(WallUtils.isValidateMySql(sql)); //
    }
    
    public void test_true_9() throws Exception {
        WallConfig config = new WallConfig();
        config.setHintAllow(true);
        config.setMultiStatementAllow(true);
        String sql = "CREATE TABLE `session` ("//
                     + " `sess_id` varchar(128) NOT NULL,"//
                     + " `created` datetime NOT NULL DEFAULT '1000-01-01 00:00:00',"//
                     + " `changed` datetime NOT NULL DEFAULT '1000-01-01 00:00:00',"//
                     + " `ip` varchar(40) NOT NULL,"//
                     + " `vars` mediumtext NOT NULL,"//
                     + " PRIMARY KEY(`sess_id`),"//
                     + " INDEX `changed_index` (`changed`)"//
                     + ") /*!40000 ENGINE=INNODB */ /*!40101 CHARACTER SET utf8 COLLATE utf8_general_ci */ ";
        Assert.assertTrue(WallUtils.isValidateMySql(sql, config)); //
    }
    
    public void test_true_10() throws Exception {
        WallConfig config = new WallConfig();
        config.setHintAllow(true);
        config.setMultiStatementAllow(true);
        config.setNoneBaseStatementAllow(true);
        String sql = "CREATE DATABASE `newsfocus` /*!40100 COLLATE 'big5_chinese_ci' */ ";
        Assert.assertTrue(WallUtils.isValidateMySql(sql, config)); //
    }

    public void test_true_11() throws Exception {
        WallConfig config = new WallConfig();
        config.setHintAllow(true);
        config.setMultiStatementAllow(true);
        config.setNoneBaseStatementAllow(true);
        String sql = "EXPLAIN /*!40100 EXTENDED */ SELECT * FROM trade_order_header WHERE id = ?";
        Assert.assertTrue(WallUtils.isValidateMySql(sql, config)); //
    }
}
