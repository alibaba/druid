package com.alibaba.druid.filter.url;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

public class HostAndPortHolder {
    private static final Log LOG = LogFactory.getLog(ConnectionConnectFilterChainImpl.class);

    public static final String UNAVAILABLE = "UNAVAILABLE_HOST";

    private static HostAndPortHolder instance = new HostAndPortHolder();

    private Map<String, List<String>> holder = new HashMap<String, List<String>>();
    private List<String> blacklist = new ArrayList<String>();

    private Random random = new Random();

    public static HostAndPortHolder getInstance() {
        return instance;
    }

    public static void loadFromProperties(String file) {
        Properties properties = new Properties();
        if (file == null) {
            return;
        }
        InputStream is;
        try {
            LOG.debug("Trying to load " + file + " from FileSystem.");
            is = new FileInputStream(file);
        } catch(FileNotFoundException e) {
            LOG.debug("Trying to load " + file + " from Classpath.");
            is = HostAndPortHolder.class.getResourceAsStream(file);
        }
        if (is != null) {
            try {
                properties.load(is);
            } catch(Exception e) {
                LOG.error("Exception occurred while loading " + file, e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
        } else {
            LOG.warn("File " + file + " can't be loaded!");
        }

        for (Object k : properties.keySet()) {
            String v = properties.getProperty((String) k);
            if (v != null && v.trim().length() > 0) {
                LOG.info("Loading placeholder " + k + " = [" + v + "]");
                List<String> values = Arrays.asList(v.split(";"));
                getInstance().putHostAndPorts((String) k, values);
            }
        }

    }

    private HostAndPortHolder() {
    }

    public void putHostAndPorts(String key, List<String> values) {
        if (key == null || values == null || values.isEmpty()) {
            return;
        }
        holder.put(key, values);
    }

    public Map<String, List<String>> getHolder() {
        return holder;
    }

    public void clearBlacklist() {
        blacklist.clear();
    }

    public void addBlacklist(String hostAndPort) {
        blacklist.add(hostAndPort);
    }

    public void removeBlacklist(String hostAndPort) {
        if (hostAndPort == null) {
            return;
        }
        blacklist.remove(hostAndPort);
    }

    public int getBlacklistSize() {
        return blacklist.size();
    }

    public String get(String name) {
        if (name == null || !holder.containsKey(name)) {
            LOG.warn("Can not find placeholder " + name + ".");
            return UNAVAILABLE;
        }

        List<String> hostAndPorts = holder.get(name);
        int idx = random.nextInt(hostAndPorts.size());
        String returnValue = hostAndPorts.get(idx);
        String hostAndPort = returnValue;
        for (int i = 0; i < hostAndPorts.size(); i++) {
            if (isAvailable(hostAndPort)) {
                returnValue = hostAndPort;
                break;
            }
            if (++idx >= hostAndPorts.size()) {
                idx = 0;
            }
            hostAndPort = hostAndPorts.get(idx);
        }
        return returnValue;
    }

    public List<String> getAll(String name) {
        if (name == null || !holder.containsKey(name)) {
            return new ArrayList<String>();
        }
        return holder.get(name);
    }

    private boolean isAvailable(String hostAndPort) {
        return !blacklist.contains(hostAndPort);
    }
}
