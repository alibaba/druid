package com.alibaba.druid.filter.wall.spi;

import com.alibaba.druid.filter.wall.WallConfig;
import com.alibaba.druid.filter.wall.WallProvider;
import com.alibaba.druid.filter.wall.WallVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;

/**
 * SQLServerProvider
 *
 * @author RaymondXiu
 * @version 1.0, 2012-3-17
 * @see
 */
public class SQLServerProvider extends WallProvider {

    /**
     * @param config
     */
    public SQLServerProvider(WallConfig config) {
        super(config);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.filter.wall.WallProvider#createParser(java.lang.String)
     */
    @Override
    public SQLStatementParser createParser(String sql) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.alibaba.druid.filter.wall.WallProvider#createWallVisitor()
     */
    @Override
    public WallVisitor createWallVisitor() {
        // TODO Auto-generated method stub
        return null;
    }

}
