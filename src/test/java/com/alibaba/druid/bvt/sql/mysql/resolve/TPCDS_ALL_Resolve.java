package com.alibaba.druid.bvt.sql.mysql.resolve;

import com.alibaba.druid.DbType;
import com.alibaba.druid.benckmark.TPCDS;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.repository.SchemaRepository;
import junit.framework.TestCase;

import java.util.List;

public class TPCDS_ALL_Resolve extends TestCase {
    private SchemaRepository repository = new SchemaRepository(DbType.mysql);

    protected void setUp() throws Exception {
        repository.acceptDDL(TPCDS.getDDL());
    }

    public void test_q01() throws Exception {
        for (int q = 1; q <= 99; ++q) {
            System.out.println("tpcds query-" + q);
            System.out.println("-----------------------------------------------------");
            String sql = TPCDS.getQuery(q);

           final List<SQLStatement> statements = SQLUtils.parseStatements(sql, DbType.mysql);

            for (SQLStatement stmt : statements) {
                repository.resolve(stmt);

                final SQLSelect select = ((SQLSelectStatement) stmt).getSelect();
                final SQLSelectQueryBlock firstQueryBlock = select.getFirstQueryBlock();
                if (firstQueryBlock == null) {
                    continue;
                }

                final List<SQLSelectItem> selectList = firstQueryBlock.getSelectList();
                for (int i = 0; i < selectList.size(); i++) {
                    SQLSelectItem selectItem = selectList.get(i);
                    if (selectItem.getExpr() instanceof SQLAllColumnExpr) {
                        continue;
                    }
                    final SQLDataType dataType = selectItem.computeDataType();
                    if (dataType == null) {
//                        fail("dataType is null : " + selectItem);
                    }
                }
            }
        }
    }

}
