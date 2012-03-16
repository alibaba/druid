package com.alibaba.druid.filter.wall;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.NotAllowCommentException;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public abstract class WallProvider {

    // Dummy value to associate with an Object in the backing Map
    private static final Object           PRESENT                       = new Object();

    private LinkedHashMap<String, Object> whiteList;

    private int                           whileListMaxSize              = 1024;

    private int                           whiteSqlMaxLength             = 1024;                 // 1k

    protected final Set<String>           permitFunctions               = new HashSet<String>();
    protected final Set<String>           permitTables                  = new HashSet<String>();
    protected final Set<String>           permitSchemas                 = new HashSet<String>();
    protected final Set<String>           permitNames                   = new HashSet<String>();
    protected final Set<String>           permitObjects                 = new HashSet<String>();

    private boolean                       checkSelectAlwayTrueCondition = true;

    public boolean isCheckSelectAlwayTrueCondition() {
        return checkSelectAlwayTrueCondition;
    }

    public void setCheckSelectAlwayTrueCondition(boolean checkSelectAlwayTrueCondition) {
        this.checkSelectAlwayTrueCondition = checkSelectAlwayTrueCondition;
    }

    public synchronized void addWhiteSql(String sql) {
        if (whiteList == null) {
            whiteList = new LinkedHashMap<String, Object>(whileListMaxSize, 0.75f, true);
        }

        whiteList.put(sql, PRESENT);
    }

    public synchronized boolean whiteContains(String sql) {
        if (whiteList == null) {
            return false;
        }

        return whiteList.get(sql) != null;
    }

    public abstract SQLStatementParser createParser(String sql);

    public abstract WallVisitor createWallVisitor();

    public boolean check(String sql, boolean throwException) throws WallRuntimeException {
        // first step, check whiteList
        boolean isWhite = whiteContains(sql);
        if (isWhite) {
            return true;
        }

        SQLStatementParser parser = createParser(sql);
        parser.getLexer().setAllowComment(false); // permit comment

        List<SQLStatement> statementList = new ArrayList<SQLStatement>();

        try {
            parser.parseStatementList(statementList);
        } catch (NotAllowCommentException e) {
            if (throwException) {
                throw new WallRuntimeException("not allow comment : " + sql);
            }

            return false;
        }

        if (parser.getLexer().token() != Token.EOF) {
            if (throwException) {
                throw new WallRuntimeException("illegal statement : " + sql);
            }

            return false;
        }
        if (statementList.size() > 1) {
            if (throwException) {
                throw new WallRuntimeException("multi-statement : " + sql);
            }

            return false;
        }

        SQLStatement stmt = statementList.get(0);

        WallVisitor visitor = createWallVisitor();

        stmt.accept(visitor);

        if (visitor.getViolations().size() > 0) {
            if (throwException) {
                Violation violation = visitor.getViolations().get(0);
                throw new WallRuntimeException(violation.toString());
            } else {
                return false;
            }
        }

        if (sql.length() < whiteSqlMaxLength) {
            this.addWhiteSql(sql);
        }

        return true;
    }

    public Set<String> getPermitFunctions() {
        return permitFunctions;
    }

    public Set<String> getPermitTables() {
        return permitTables;
    }

    public Set<String> getPermitSchemas() {
        return permitSchemas;
    }

    public Set<String> getPermitNames() {
        return permitNames;
    }

    public Set<String> getPermitObjects() {
        return permitObjects;
    }

}
