package com.alibaba.druid.support.calcite;

import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;

/**
 * @author lijun.cailj 2017/11/21
 */
public class CalciteSqlBasicCall extends SqlBasicCall {

    public CalciteSqlBasicCall(SqlOperator operator, SqlNode[] operands, SqlParserPos pos) {
        super(operator, operands, pos);
    }

    public CalciteSqlBasicCall(SqlOperator operator, SqlNode[] operands, SqlParserPos pos, boolean expanded,
                                  SqlLiteral functionQualifier) {
        super(operator, operands, pos, expanded, functionQualifier);
    }
}
