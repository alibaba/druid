package com.alibaba.druid.filter.wall.spi;

import java.util.ArrayList;
import java.util.List;
import com.alibaba.druid.filter.wall.Violation;
import com.alibaba.druid.filter.wall.WallConfig;
import com.alibaba.druid.filter.wall.WallVisitor;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitorAdapter;


public class SQLServerWallVisitor extends SQLServerASTVisitorAdapter implements WallVisitor, SQLServerASTVisitor{

    private final WallConfig      config;
    private final List<Violation> violations = new ArrayList<Violation>();
    
    /**
     * @param config
     */
    public SQLServerWallVisitor(WallConfig config) {
        this.config = config;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.filter.wall.WallVisitor#getConfig()
     */
    @Override
    public WallConfig getConfig() {
        return this.config;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.filter.wall.WallVisitor#getViolations()
     */
    @Override
    public List<Violation> getViolations() {
        return violations;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.filter.wall.WallVisitor#isPermitTable(java.lang.String)
     */
    @Override
    public boolean isPermitTable(String name) {
        if(!config.isTableCheck()){
            return false;
        }
        
        name = WallVisitorUtils.form(name);
        return config.getPermitTables().contains(name);
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.filter.wall.WallVisitor#toSQL(com.alibaba.druid.sql.ast.SQLObject)
     */
    @Override
    public String toSQL(SQLObject obj) {
        return SQLUtils.toMySqlString(obj);
    }

}
