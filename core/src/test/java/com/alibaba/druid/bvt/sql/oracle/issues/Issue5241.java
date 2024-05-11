package com.alibaba.druid.bvt.sql.oracle.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * 验证 Oracle union sql的问题
 *
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5241">Issue来源</a>
 */
public class Issue5241 {

    @Test
    public void test_parse_union() throws Exception {
        for (DbType dbType : new DbType[]{
            //DbType.mysql,
            //DbType.oracle,
            DbType.postgresql,
            DbType.oscar,
        }) {
            for (String sql : new String[]{
                "select count(*)" +
                    "from (" +
                    "    (select id1111 from tb_user111 where id in(1,2) order by id)" +
                    "    union" +
                    "    (select id2222 from tb_user222 where id=4 )" +
                    "    union" +
                    "    (select id3333 from tb_user333 where id=5 )" +
                    "" +
                    ") t",
                "select *\n" +
                    "from (\n" +
                    "    (select * from tb_user6666 where id in(1,2) order by id )\n" +
                    "         union (select id from tb_user7777 where id=5 order by id)\n" +
                    "    \n" +
                    "    union\n" +
                    "    select * from tb_user8888 where id=4 order by id desc\n" +
                    "\n" +
                    ") t;",
            }) {
                SQLParseAssertUtil.assertParseSql(sql, dbType);
            }
        }
    }
}
