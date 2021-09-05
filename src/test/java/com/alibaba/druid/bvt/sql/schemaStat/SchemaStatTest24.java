package com.alibaba.druid.bvt.sql.schemaStat;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class SchemaStatTest24 extends TestCase {
    SchemaRepository repository;

    protected void setUp() throws Exception {
        repository = new SchemaRepository(JdbcConstants.ODPS);
    }

    public void test_0() throws Exception {
        String sql = "clone table t1 to t2;";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.ODPS);
        SQLStatement stmt = parser.parseStatementList().get(0);

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(repository);
        stmt.accept(statVisitor);

        System.out.println("Tables : " + statVisitor.getTables());
        System.out.println(statVisitor.getColumns());
//        System.out.println(statVisitor.getGroupByColumns()); // group by
        System.out.println("relationships : " + statVisitor.getRelationships()); // group by
        System.out.println(statVisitor.getConditions());

        assertEquals(0, statVisitor.getColumns().size());
        assertEquals(0, statVisitor.getConditions().size());
        assertEquals(0, statVisitor.getFunctions().size());

        assertTrue(statVisitor.containsTable("t1"));
        assertTrue(statVisitor.containsTable("t2"));
    }

    public void test_1() throws Exception {
        String sql = "MERGE INTO t1 AS t USING (select * from ods_dayima_topic_delta where pt='20210831') AS s \n" +
                "ON s.id = t.id \n" +
                "WHEN matched THEN UPDATE SET t.id=s.id,t.topic_category_id=s.topic_category_id,t.uid=s.uid,t.title=s.title,t.content=s.content,t.STATUS=s.STATUS,t.dateline=s.dateline,t.ip=s.ip,t.replynum=s.replynum,t.lastreplytime=s.lastreplytime,t.settop=s.settop,t.lastoptime=s.lastoptime,t.LOCK=s.LOCK,t.setbottom=s.setbottom,t.flag=s.flag,t.digest=s.digest,t.createtime=s.createtime,t.lastpost=s.lastpost,t.favnum=s.favnum,t.attach_pictures=s.attach_pictures,t.viewnum=s.viewnum,t.settoptime=s.settoptime,t.hidden=s.hidden,t.TYPE=s.TYPE,t.hashtag_code=s.hashtag_code,t.like_num=s.like_num,t.join_num=s.join_num,t.is_anonymous=s.is_anonymous,t.has_goods=s.has_goods,t.extra_info=s.extra_info,t.created=s.created\n" +
                "WHEN NOT matched THEN INSERT VALUES(s.id,s.topic_category_id,s.uid,s.title,s.content,s.STATUS,s.dateline,s.ip,s.replynum,s.lastreplytime,s.settop,s.lastoptime,s.LOCK,s.setbottom,s.flag,s.digest,s.createtime,s.lastpost,s.favnum,s.attach_pictures,s.viewnum,s.settoptime,s.hidden,s.TYPE,s.hashtag_code,s.like_num,s.join_num,s.is_anonymous,s.has_goods,s.extra_info,s.created);";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.ODPS);
        SQLStatement stmt = parser.parseStatementList().get(0);

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(repository);
        stmt.accept(statVisitor);

        System.out.println("Tables : " + statVisitor.getTables());
        System.out.println(statVisitor.getColumns());
//        System.out.println(statVisitor.getGroupByColumns()); // group by
        System.out.println("relationships : " + statVisitor.getRelationships()); // group by
        System.out.println(statVisitor.getConditions());

        assertEquals(34, statVisitor.getColumns().size());
//        assertEquals(1, statVisitor.getConditions().size());
        assertEquals(0, statVisitor.getFunctions().size());

        assertTrue(statVisitor.containsTable("t1"));
        assertTrue(statVisitor.containsColumn("t1", "id"));
    }

    public void test_2() throws Exception {
        String sql = "unload from t  partition  (ds='20210829')\n" +
                "into\n" +
                "location 'oss://oss-cn-shanghai-internal.aliyuncs.com/xx/xxx'\n" +
                "stored by 'com.aliyun.odps.TsvStorageHandler'\n" +
                "with serdeproperties ('odps.properties.rolearn'='acs:ram::123:role/xxx', 'odps.text.option.gzip.output.enabled'='true');";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.ODPS);
        SQLStatement stmt = parser.parseStatementList().get(0);

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(repository);
        stmt.accept(statVisitor);

        System.out.println("Tables : " + statVisitor.getTables());
        System.out.println(statVisitor.getColumns());
//        System.out.println(statVisitor.getGroupByColumns()); // group by
        System.out.println("relationships : " + statVisitor.getRelationships()); // group by
        System.out.println(statVisitor.getConditions());

        assertEquals(1, statVisitor.getColumns().size());
        assertEquals(0, statVisitor.getConditions().size());
        assertEquals(0, statVisitor.getFunctions().size());

        assertTrue(statVisitor.containsTable("t"));
//        assertTrue(statVisitor.containsColumn("t", "f1"));
//        assertEquals("t.f1 < 4", statVisitor.getConditions().get(0).toString());
    }

    public void test_3() throws Exception {
        String sql = "analyze table t partition(ds='20210829') compute statistics for columns (member_ids);";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.ODPS);
        SQLStatement stmt = parser.parseStatementList().get(0);

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(repository);
        stmt.accept(statVisitor);

        System.out.println("Tables : " + statVisitor.getTables());
        System.out.println(statVisitor.getColumns());
//        System.out.println(statVisitor.getGroupByColumns()); // group by
        System.out.println("relationships : " + statVisitor.getRelationships()); // group by
        System.out.println(statVisitor.getConditions());

        assertEquals(1, statVisitor.getColumns().size());
        assertEquals(0, statVisitor.getConditions().size());
        assertEquals(0, statVisitor.getFunctions().size());

        assertTrue(statVisitor.containsTable("t"));
        assertTrue(statVisitor.containsColumn("t", "ds"));
    }

    public void test_4() throws Exception {
        String sql = "count t PARTITION (type='20210830');";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.ODPS);
        SQLStatement stmt = parser.parseStatementList().get(0);

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(repository);
        stmt.accept(statVisitor);

        System.out.println("Tables : " + statVisitor.getTables());
        System.out.println(statVisitor.getColumns());
//        System.out.println(statVisitor.getGroupByColumns()); // group by
        System.out.println("relationships : " + statVisitor.getRelationships()); // group by
        System.out.println(statVisitor.getConditions());

        assertEquals(1, statVisitor.getColumns().size());
        assertEquals(0, statVisitor.getConditions().size());
        assertEquals(0, statVisitor.getFunctions().size());

        assertTrue(statVisitor.containsTable("t"));
        assertTrue(statVisitor.containsColumn("t", "type"));
    }

    public void test_5() throws Exception {
        String sql = "EXSTORE t PARTITION(ds='20210901');";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.ODPS);
        SQLStatement stmt = parser.parseStatementList().get(0);

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(repository);
        stmt.accept(statVisitor);

        System.out.println("Tables : " + statVisitor.getTables());
        System.out.println(statVisitor.getColumns());
//        System.out.println(statVisitor.getGroupByColumns()); // group by
        System.out.println("relationships : " + statVisitor.getRelationships()); // group by
        System.out.println(statVisitor.getConditions());

        assertEquals(1, statVisitor.getColumns().size());
        assertEquals(0, statVisitor.getConditions().size());
        assertEquals(0, statVisitor.getFunctions().size());

        assertTrue(statVisitor.containsTable("t"));
        assertTrue(statVisitor.containsColumn("t", "ds"));
    }

    public void test_6() throws Exception {
        String sql = "alter table t partition(pt='20210827',pval='0to30')\n" +
                "rename to partition(pt='20210829',pval='0to30');";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.ODPS);
        SQLStatement stmt = parser.parseStatementList().get(0);

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(repository);
        stmt.accept(statVisitor);

        System.out.println("Tables : " + statVisitor.getTables());
        System.out.println(statVisitor.getColumns());
//        System.out.println(statVisitor.getGroupByColumns()); // group by
        System.out.println("relationships : " + statVisitor.getRelationships()); // group by
        System.out.println(statVisitor.getConditions());

        assertEquals(2, statVisitor.getColumns().size());
        assertEquals(0, statVisitor.getConditions().size());
        assertEquals(0, statVisitor.getFunctions().size());

        assertTrue(statVisitor.containsTable("t"));
        assertTrue(statVisitor.containsColumn("t", "pt"));
    }

}
