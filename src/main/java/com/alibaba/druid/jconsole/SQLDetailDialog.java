package com.alibaba.druid.jconsole;

import java.util.List;

import javax.management.openmbean.CompositeData;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.springframework.util.Assert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;

public class SQLDetailDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private JTextArea         textArea;

    public SQLDetailDialog(CompositeData rowData){
        textArea = new JTextArea();
        JScrollPane textAreaScrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.getContentPane().add(textAreaScrollPane);

        String sql = (String) rowData.get("SQL");

        sql = format(sql);

        textArea.setText(sql);
    }

    public static String format(String sql) {
        if (sql == null || sql.length() == 0) {
            return sql;
        }

        try {
            return mergeMySql(sql);
        } catch (Exception ex) {
            // skip
        }

        try {
            return mergeOracle(sql);
        } catch (Exception ex) {
            // skip
        }

        return sql; // 返回原来的SQL
    }

    public static String mergeMySql(String sql) {
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.isTrue(1 == statementList.size());

        StringBuilder out = new StringBuilder();
        MySqlOutputVisitor visitor = new MySqlOutputVisitor(out);
        statemen.accept(visitor);

        return out.toString();
    }

    public static String mergeOracle(String sql) {
        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.isTrue(1 == statementList.size());

        StringBuilder out = new StringBuilder();
        OracleOutputVisitor visitor = new OracleOutputVisitor(out);
        statemen.accept(visitor);

        return out.toString();
    }
}
