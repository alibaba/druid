package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

/**
 * Created by wenshao on 16/8/5.
 */
public class MySqlCeateTableTest34 extends TestCase {
    public void test_for_parse() throws Exception {
        String sql = "CREATE TABLE \"item_extra\" (\n" +
                "  \"id\" int(10) unsigned NOT NULL AUTO_INCREMENT,\n" +
                "  \"item_id\" int(10) unsigned NOT NULL DEFAULT '0' COMMENT '商品id',\n" +
                "  \"type\" tinyint(4) unsigned NOT NULL DEFAULT '0' COMMENT '扩展属性类型，0：虚拟商品',\n" +
                "  \"attr_key\" varchar(50) NOT NULL COMMENT '扩展属性key',\n" +
                "  \"attr_value\" varchar(500) NOT NULL COMMENT '扩展属性value',\n" +
                "  PRIMARY KEY (\"id\"),\n" +
                "  KEY \"idx_item_type_kv\" (\"item_id\",\"type\",\"attr_key\",\"attr_value\"(191))\n" +
                ")"; //

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
//        print(statementList);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(5, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("item_extra")));

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE TABLE \"item_extra\" (\n" +
                "\t\"id\" int(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
                "\t\"item_id\" int(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '商品id',\n" +
                "\t\"type\" tinyint(4) UNSIGNED NOT NULL DEFAULT '0' COMMENT '扩展属性类型，0：虚拟商品',\n" +
                "\t\"attr_key\" varchar(50) NOT NULL COMMENT '扩展属性key',\n" +
                "\t\"attr_value\" varchar(500) NOT NULL COMMENT '扩展属性value',\n" +
                "\tPRIMARY KEY (\"id\"),\n" +
                "\tKEY \"idx_item_type_kv\" (\"item_id\", \"type\", \"attr_key\", \"attr_value\"(191))\n" +
                ")", output);
    }
}
