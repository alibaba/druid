package com.alibaba.druid.benckmark.sql;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by kaiwang.ckw on 15/05/2017.
 */
public class Templates {

    public static final String UNKNOWN = "UNKNOWN";

    public static String parameterize(SQLStatement ast, Set<String> physicalNames, List<Object> params) {

        List<Object> parameters = null;
        Appendable out = new StringBuilder();
        SQLASTOutputVisitor visitor = new MySqlOutputVisitor(out);
        visitor.setParameterized(true);
        visitor.setParameterizedMergeInList(true);
        if (physicalNames != null) {
            visitor.setExportTables(true);
        }
        if (params != null) {
            parameters = new ArrayList<Object>();
            visitor.setParameters(parameters);
        }
        visitor.setPrettyFormat(false);
        ast.accept(visitor);

        if (physicalNames != null) {
            Set<String> tableSet = visitor.getTables();
            if (tableSet != null) {
                physicalNames.addAll(tableSet);
            }
        }
        if (params != null) {
            if (!parameters.isEmpty()) {
                params.addAll(parameters);
            }
        }

        String sqlTemplate = out.toString();
        return sqlTemplate;
    }

}
