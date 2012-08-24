/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.pool.ha.cobar;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.druid.pool.ha.DataSourceHolder;
import com.alibaba.druid.pool.ha.config.ConfigLoader;
import com.alibaba.druid.pool.ha.config.URLConnectionConfigLoader;
import com.alibaba.druid.support.json.JSONUtils;

public class CobarConfigLoader extends URLConnectionConfigLoader implements ConfigLoader {

    private final CobarDataSource dataSource;

    public CobarConfigLoader(CobarDataSource cobarDataSource) throws SQLException{
        super(createURL(cobarDataSource.getUrl()));

        this.dataSource = cobarDataSource;
    }

    public CobarDataSource getDataSource() {
        return dataSource;
    }
    
    public static boolean isCobar(String url) {
        return url.startsWith("jdbc:cobar://");
    }

    public static URL createURL(String jdbcUrl) throws SQLException {
        if (!isCobar(jdbcUrl)) {
            throw new SQLException("illegal cobar url");
        }

        // jdbc:cobar://ip:port/sid
        String rest = jdbcUrl.substring("jdbc:cobar://".length());
        String[] items = rest.split("/");
        String ip = items[0];
        String sid = items[1];

        int port = 80;
        {
            int pos = ip.indexOf(':');
            if (pos != -1) {
                port = Integer.parseInt(ip.substring(pos + 1));
                ip = ip.substring(0, pos);
            }
        }

        //String url = "http://" + ip + ":" + port + "/cobarStatusQuery?sid=" + sid;
        String url = "http://" + ip + ":" + port + "/cobarStatusQuery?sid=" + sid;

        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new SQLException(e.getMessage(), e);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected void handleResponseMessage() throws SQLException {
        String responseMessage = this.getResponseMessage();
        
        Map<String, Object> json = (Map<String, Object>) JSONUtils.parse(responseMessage);
        String errorMessage = (String) json.get("error");
        if (errorMessage != null) {
            throw new SQLException("load config error, message : " + errorMessage);
        }

        List array = (List) json.get("cobarList");

        Set<String> keys = new HashSet<String>();
        for (int i = 0; i < array.size(); ++i) {
            Map<String, Object> item = (Map<String, Object>) array.get(i);

            String ip = (String) item.get("ip");
            int port = (Integer) item.get("port");
            String schema = (String) item.get("schema");
            int weight = (Integer) item.get("weight");

            String key = ip + ":" + port + "/" + schema;
            keys.add(key);

            DataSourceHolder holder = dataSource.getDataSourceHolder(key);

            if (holder == null) {
                String jdbcUrl = createJdbcUrl(ip, port, schema);
                holder = dataSource.createDataSourceHolder(jdbcUrl, weight);
                dataSource.addDataSource(key, holder);
            } else {
                if (holder.getWeight() != weight) {
                    holder.setWeight(weight);
                    dataSource.afterDataSourceChanged(null);
                }
            }
        }

        int removeCount = 0;
        Iterator<Map.Entry<String, DataSourceHolder>> iter = dataSource.getDataSources().entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, DataSourceHolder> entry = iter.next();
            if (!keys.contains(entry.getKey())) {
                iter.remove();
                dataSource.handleDataSourceDiscard(entry.getValue());
                removeCount++;
            }
        }
        if (removeCount != 0) {
            dataSource.afterDataSourceChanged(null);
        }
    }

    protected String createJdbcUrl(String ip, int port, String schema) {
        return "jdbc:mysql://" + ip + ":" + port + "/" + schema;
    }

}
