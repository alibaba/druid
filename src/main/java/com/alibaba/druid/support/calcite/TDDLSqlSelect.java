package com.alibaba.druid.support.calcite;

import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParserPos;

/**
 * 扩展calcite，支持select 中的hints
 *
 * @author lijun.cailj 2017/11/29
 */
public class TDDLSqlSelect extends SqlSelect {

    private SqlNodeList hints;
    private SqlNodeList headHints;

    public TDDLSqlSelect(SqlParserPos pos, SqlNodeList keywordList, SqlNodeList selectList, SqlNode from, SqlNode where,
                         SqlNodeList groupBy, SqlNode having, SqlNodeList windowDecls, SqlNodeList orderBy,
                         SqlNode offset, SqlNode fetch, SqlNodeList hints, SqlNodeList headHints) {
        super(pos, keywordList, selectList, from, where, groupBy, having, windowDecls, orderBy, offset, fetch);
        this.hints = hints;
        this.headHints = headHints;
    }

    public SqlNodeList getHints() {
        return hints;
    }

    public void setHints(SqlNodeList hints) {
        this.hints = hints;
    }

    public SqlNodeList getHeadHints() {
        return headHints;
    }

    public void setHeadHints(SqlNodeList headHints) {
        this.headHints = headHints;
    }
}
