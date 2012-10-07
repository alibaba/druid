package com.alibaba.druid.support.profile;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProfileStat {

    private ConcurrentHashMap<ProfileEntryKey, ProfileEntryStat> entries = new ConcurrentHashMap<ProfileEntryKey, ProfileEntryStat>();

    public ConcurrentHashMap<ProfileEntryKey, ProfileEntryStat> getEntries() {
        return entries;
    }

    public void record(Map<ProfileEntryKey, ProfileEntryReqStat> requestStatsMap) {
        if (requestStatsMap == null) {
            return;
        }
        
        for (Map.Entry<ProfileEntryKey, ProfileEntryReqStat> entry : requestStatsMap.entrySet()) {
            ProfileEntryKey entryKey = entry.getKey();
            ProfileEntryReqStat reqEntryStat = entry.getValue();

            ProfileEntryStat entryStat = entries.get(entryKey);
            if (entryStat == null) {
                entries.putIfAbsent(entryKey, new ProfileEntryStat());
                entryStat = entries.get(entryKey);
            }

            entryStat.addExecuteCount(reqEntryStat.getExecuteCount());
            entryStat.addExecuteTimeNanos(reqEntryStat.getExecuteTimeNanos());
        }
    }

    public Map<Map<String, Object>, Map<String, Object>> getStatData() {
        Map<Map<String, Object>, Map<String, Object>> data = new LinkedHashMap<Map<String, Object>, Map<String, Object>>();

        for (Map.Entry<ProfileEntryKey, ProfileEntryStat> entry : entries.entrySet()) {
            Map<String, Object> key = new LinkedHashMap<String, Object>();
            Map<String, Object> value = new LinkedHashMap<String, Object>();

            key.put("Parent", entry.getKey().getParentName());
            key.put("Name", entry.getKey().getName());
            key.put("Type", entry.getKey().getType());

            value.put("ExecuteCount", entry.getValue().getExecuteCount());
            value.put("ExecuteTimeNanos", entry.getValue().getExecuteTimeNanos());

            data.put(key, value);
        }

        return data;
    }

}
