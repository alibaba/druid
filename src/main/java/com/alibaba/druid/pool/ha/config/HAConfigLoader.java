package com.alibaba.druid.pool.ha.config;

import java.net.URL;
import java.sql.SQLException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


public class HAConfigLoader extends URLConnectionConfigLoader {

    public HAConfigLoader(URL configServerURL){
        super(configServerURL);
    }

    @Override
    protected void handleResponseMessage() throws SQLException {
        JSONObject json = JSON.parseObject(this.getResponseMessage());
        json.get("serverList");
    }

}
