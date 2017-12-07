package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Issue1935 extends TestCase {
    public void test_for_issue() throws Exception {
        String DBTYPE = JdbcConstants.MYSQL;
        //String sql = "select name, course ,scole from student inner join scole on student.id = scole.sd_id where course = '数学' limit 10;";
        //sql = "select name,course,sum(scole) as total from student where student.id in (select sd_id from scole where name='aaa') and scole in (1,2,3) group by name HAVING total <60 order by scole desc limit 10 ,2 ";
        String sql = "select name from  student where id in (select sd_id from scole where scole < 60 order by scole asc) or id = 2 order by name desc";
        String format = SQLUtils.format(sql, DBTYPE);
        //System.out.println("formated sql :  " + format);
        List<SQLStatement> list = SQLUtils.parseStatements(sql, DBTYPE);

        for (int i = 0; i < list.size(); i++) {
            SQLStatement stmt = list.get(i);
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            stmt.accept(visitor);


            //获取操作方法名称,依赖于表名称
            System.out.println("涉及到的所有表 : " + visitor.getTables());
            Map<TableStat.Name, TableStat> table_map = visitor.getTables();
            for(Map.Entry<TableStat.Name, TableStat> entry : table_map.entrySet()){
                TableStat.Name name = entry.getKey();
                name.getName();
                //存储表的调度次数，包括select ，update等
                TableStat ts = entry.getValue();

            }
            //获取字段名称
            System.out.println( visitor.getParameters());
            //获取列名
            System.out.println("查询的列信息 : " + visitor.getColumns());
            Collection<TableStat.Column> cc = visitor.getColumns();
            //column 存储了表名，列名，以及列是出现的位置，where，select，groupby ，order
            for(TableStat.Column column : cc){

            }
            System.out.println("conditions : " + visitor.getConditions() );
            List<TableStat.Condition> conditions = visitor.getConditions();
            System.out.println("----------------------------");
            for(TableStat.Condition cond : conditions){
                System.out.println( "column : " + cond.getColumn());
                System.out.println( "operator : " + cond.getOperator());
                System.out.println( "values  : " + cond.getValues());

                System.out.println("----------------------------");
            }
            System.out.println("group by : " + visitor.getGroupByColumns() );
            System.out.println("order by : " + visitor.getOrderByColumns() );
            System.out.println("relations ships  : " + visitor.getRelationships() );

        }

    }
}
