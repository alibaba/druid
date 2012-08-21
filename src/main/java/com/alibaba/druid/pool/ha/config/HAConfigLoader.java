package com.alibaba.druid.pool.ha.config;

import java.net.URL;
import java.sql.SQLException;
import java.util.Map;

import com.alibaba.druid.support.json.JSONUtils;

public class HAConfigLoader extends URLConnectionConfigLoader {

    public HAConfigLoader(URL configServerURL){
        super(configServerURL);
    }

    @Override
    protected void handleResponseMessage() throws SQLException {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) JSONUtils.parse(this.getResponseMessage());
        map.get("serverList");
    }

}
