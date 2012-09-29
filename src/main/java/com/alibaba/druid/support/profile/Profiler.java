package com.alibaba.druid.support.profile;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.support.profile.ProfileEntry.Key;

public class Profiler {

    public static ThreadLocal<Boolean>                              enableLocal    = new ThreadLocal<Boolean>();

    private static ThreadLocal<Map<ProfileEntry.Key, ProfileEntry>> entriesLocal = new ThreadLocal<Map<Key, ProfileEntry>>();

    private final static ThreadLocal<ProfileEntry>                  currentLocal   = new ThreadLocal<ProfileEntry>();

    public static void setThreadLocalEnable() {
        enableLocal.set(true);
    }

    public static void setThreadLocalDisable() {
        enableLocal.remove();
    }

    public static boolean isEnable() {
        return Boolean.TRUE == enableLocal.get();
    }

    public static void enter(String name, String type, long startNano) {
        ProfileEntry parent = currentLocal.get();
        String parentName = null;
        if (parent != null) {
            parentName = parent.getKey().getName();
        }
        
        ProfileEntry.Key key = new ProfileEntry.Key(parentName, name);
        
        Map<ProfileEntry.Key, ProfileEntry> entries = entriesLocal.get();
        if (entries == null) {
            entries = new HashMap<ProfileEntry.Key, ProfileEntry>();
            entriesLocal.set(entries);
        }
        
        ProfileEntry entry = entries.get(key);
        if (entry == null) {
            entry = new ProfileEntry(key, type);
            entries.put(key, entry);
        }
        
        currentLocal.set(entry);
    }

    public static ProfileEntry current() {
        return currentLocal.get();
    }

    public static void release(long nanoSpan) {

    }
}
