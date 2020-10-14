package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;

public class MySqlCreateTableTest159
        extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE `articles` (\n" +
                "  `article_id` bigint NOT NULL AUTO_INCREMENT COMMENT '文章id',\n" +
                "  `type` tinyint(1) NOT NULL COMMENT '文章类型',\n" +
                "  `cate` tinyint(1) DEFAULT '0' COMMENT '分类',\n" +
                "  `carousel` tinyint(1) DEFAULT '0' COMMENT '轮播',\n" +
                "  `author_id` bigint NOT NULL COMMENT '小编',\n" +
                "  `teacher_id` int DEFAULT NULL COMMENT '课程讲师',\n" +
                "  `is_fee` tinyint(1) DEFAULT NULL COMMENT '付费文章',\n" +
                "  `title` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章标题',\n" +
                "  `name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '大咖名字',\n" +
                "  `sub_title` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '副标题',\n" +
                "  `summary` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章简介',\n" +
                "  `tags` json DEFAULT NULL COMMENT '标签',\n" +
                "  `seo` json DEFAULT NULL COMMENT 'seo关键词',\n" +
                "  `published_at` datetime NOT NULL COMMENT '发布时间',\n" +
                "  `cover` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '封面图',\n" +
                "  `carousel_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '轮播图片',\n" +
                "  `act_time` datetime DEFAULT NULL COMMENT '活动时间',\n" +
                "  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '活动地点',\n" +
                "  `body` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章内容',\n" +
                "  `suppliers` json DEFAULT NULL COMMENT '关联服务商',\n" +
                "  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                "  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',\n" +
                "  PRIMARY KEY (`article_id`),\n" +
                "  KEY `articles_type` (`type`),\n" +
                "  KEY `articles_cate` (`cate`),\n" +
                "  KEY `articles_carousel` (`carousel`),\n" +
                "  KEY `articles_teacher_id` (`teacher_id`),\n" +
                "  KEY `articles_author_id` (`author_id`),\n" +
                "  KEY `articles_published_at` (`published_at`),\n" +
                "  KEY `articles_title` (`title`),\n" +
                "  KEY `articles_tags` ((cast(json_extract(`tags`,_utf8mb4'$[*]') as char(40) array)))\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=1054 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章'";

//        SQLCreateTableStatement stmt = (SQLCreateTableStatement) SQLUtils.parseSingleMysqlStatement(sql);
//
//        assertTrue(stmt.isPrimaryColumn("id"));
//        assertTrue(stmt.isPrimaryColumn("`id`"));
    }
}