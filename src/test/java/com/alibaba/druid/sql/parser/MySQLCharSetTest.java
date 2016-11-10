package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

/**
 * Created by tianzhen.wtz on 2015/10/31.
 * 类说明：
 */
public class MySQLCharSetTest extends TestCase{

    public void testCreateCharset(){
        String  targetSql="CREATE TABLE `test_idb`.`acct_certificate` (\n" +
                "    `id` bigint(20) NOT NULL auto_increment COMMENT '',\n" +
                "    `nodeid` varchar(5) CHARSET `gbk` COLLATE `gbk_chinese_ci` NULL COMMENT '',\n" +
                "    `certificatetype` char(1) CHARSET `gbk` COLLATE `gbk_chinese_ci` NULL COMMENT '',\n" +
                "    `certificateno` varchar(32) CHARSET `gbk` COLLATE `gbk_chinese_ci` NULL COMMENT '',\n" +
                "    PRIMARY KEY(`id`),\n" +
                "    INDEX `id_acct_certificate_nodeid`(`nodeid`),\n" +
                "    INDEX `id_acct_certificate_certificateno`(`certificateno`)\n" +
                "\n" +
                ") engine= InnoDB DEFAULT CHARSET= `gbk` DEFAULT COLLATE `gbk_chinese_ci` comment= '' ;";

        String  resultSql="CREATE TABLE `test_idb`.`acct_certificate` (\n" +
                "\t`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '', \n" +
                "\t`nodeid` varchar(5) CHARSET `gbk`  COLLATE `gbk_chinese_ci` NULL COMMENT '', \n" +
                "\t`certificatetype` char(1) CHARSET `gbk`  COLLATE `gbk_chinese_ci` NULL COMMENT '', \n" +
                "\t`certificateno` varchar(32) CHARSET `gbk`  COLLATE `gbk_chinese_ci` NULL COMMENT '', \n" +
                "\tPRIMARY KEY (`id`), \n" +
                "\tINDEX `id_acct_certificate_nodeid`(`nodeid`), \n" +
                "\tINDEX `id_acct_certificate_certificateno`(`certificateno`)\n" +
                ") ENGINE = InnoDB CHARSET = `gbk` COLLATE = `gbk_chinese_ci` COMMENT = ''";
        equal(targetSql, resultSql);
    }


    public void testAlterCharset(){
        String  targetSql="ALTER TABLE acct_certificate MODIFY COLUMN `nodeid` varchar(5) CHARSET `gbk` COLLATE `gbk_chinese_ci` NULL COMMENT ''";

        String  resultSql="ALTER TABLE acct_certificate\n" +
                "\tMODIFY COLUMN `nodeid` varchar(5) CHARSET `gbk`  COLLATE `gbk_chinese_ci` NULL COMMENT ''";
        equal(targetSql, resultSql);
    }


    private void equal(String targetSql,String resultSql){
        MySqlStatementParser parser=new MySqlStatementParser(targetSql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        System.out.println(sqlStatements.get(0).toString());
        Assert.assertTrue(sqlStatements.get(0).toString().equals(resultSql));

    }
}
