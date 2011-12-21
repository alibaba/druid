package com.alibaba.druid.pool.ha.cobar;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.alibaba.druid.pool.ha.DataSourceHolder;
import com.alibaba.druid.pool.ha.config.ConfigLoader;
import com.alibaba.druid.pool.ha.config.URLConnectionConfigLoader;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
                ip = ip.substring(0, pos);
                port = Integer.parseInt(ip.substring(pos + 1));
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

    @Override
    protected void handleResponseMessage(String response) throws SQLException {
        JSONObject json = JSON.parseObject(response);
        String errorMessage = json.getString("error");
        if (errorMessage != null) {
            throw new SQLException("load config error, message : " + errorMessage);
        }

        JSONArray array = json.getJSONArray("cobarList");

        Set<String> keys = new HashSet<String>();
        for (int i = 0; i < array.size(); ++i) {
            JSONObject item = array.getJSONObject(i);

            String ip = item.getString("ip");
            int port = item.getIntValue("port");
            String schema = item.getString("schema");
            int weight = item.getIntValue("weight");

            String key = ip + ":" + port + "/" + schema;
            keys.add(key);

            DataSourceHolder holder = dataSource.getDataSourceHolder(key);

            if (holder == null) {
                String jdbcUrl = "jdbc:mysql://" + ip + ":" + port + "/" + schema;
                holder = dataSource.createDataSourceHolder(jdbcUrl, weight);
                dataSource.addDataSource(key, holder);
            } else {
                if (holder.getWeight() != weight) {
                    holder.setWeight(weight);
                    dataSource.computeTotalWeight();
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
            dataSource.computeTotalWeight();
        }
    }

}
