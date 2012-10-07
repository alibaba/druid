package com.alibaba.druid.support.profile;

import java.util.HashMap;
import java.util.Map;

public class Profiler {

    public static final String                                            PROFILE_TYPE_WEB    = "WEB";
    public static final String                                            PROFILE_TYPE_SPRING = "SPRING";
    public static final String                                            PROFILE_TYPE_SQL    = "SQL";

    private static ThreadLocal<Map<ProfileEntryKey, ProfileEntryReqStat>> statsMapLocal       = new ThreadLocal<Map<ProfileEntryKey, ProfileEntryReqStat>>();

    private final static ThreadLocal<ProfileEntry>                        currentLocal        = new ThreadLocal<ProfileEntry>();

    public static boolean isEnable() {
        return statsMapLocal != null;
    }

    public static void enter(String name, String type) {
        if (!isEnable()) {
            return;
        }

        ProfileEntry parent = currentLocal.get();
        String parentName = null;
        if (parent != null) {
            parentName = parent.getName();
        }

        ProfileEntryKey key = new ProfileEntryKey(parentName, name, type);
        ProfileEntry entry = new ProfileEntry(parent, key);

        currentLocal.set(entry);
    }

    public static ProfileEntry current() {
        return currentLocal.get();
    }

    public static void release(long nanos) {
        ProfileEntry current = currentLocal.get();
        currentLocal.set(current.getParent());

        ProfileEntryReqStat stat = null;
        Map<ProfileEntryKey, ProfileEntryReqStat> statsMap = statsMapLocal.get();
        if (statsMap == null) {
            return;
        }

        stat = statsMap.get(current.getKey());

        if (stat == null) {
            stat = new ProfileEntryReqStat();
            statsMap.put(current.getKey(), stat);
        }

        stat.incrementExecuteCount();
        stat.addExecuteTimeNanos(nanos);
    }

    public static Map<ProfileEntryKey, ProfileEntryReqStat> getStatsMap() {
        return statsMapLocal.get();
    }

    public static void initLocal() {
        statsMapLocal.set(new HashMap<ProfileEntryKey, ProfileEntryReqStat>());
    }

    public static void removeLocal() {
        statsMapLocal.remove();
    }
}
