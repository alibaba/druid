/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.support.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ProfileStat {

    private Map<ProfileEntryKey, ProfileEntryStat> entries = new LinkedHashMap<ProfileEntryKey, ProfileEntryStat>(4);

    private ReadWriteLock                          lock    = new ReentrantReadWriteLock();

    public Map<ProfileEntryKey, ProfileEntryStat> getEntries() {
        return entries;
    }

    public void record(Map<ProfileEntryKey, ProfileEntryReqStat> requestStatsMap) {
        if (requestStatsMap == null) {
            return;
        }

        for (Map.Entry<ProfileEntryKey, ProfileEntryReqStat> entry : requestStatsMap.entrySet()) {
            ProfileEntryKey entryKey = entry.getKey();
            ProfileEntryReqStat reqEntryStat = entry.getValue();

            ProfileEntryStat entryStat = getProfileEntry(entryKey);

            entryStat.addExecuteCount(reqEntryStat.getExecuteCount());
            entryStat.addExecuteTimeNanos(reqEntryStat.getExecuteTimeNanos());
        }
    }

    private ProfileEntryStat getProfileEntry(ProfileEntryKey entryKey) {

        lock.readLock().lock();
        try {
            ProfileEntryStat entryStat = entries.get(entryKey);
            if (entryStat != null) {
                return entryStat;
            }
        } finally {
            lock.readLock().unlock();
        }

        lock.writeLock().lock();
        try {
            ProfileEntryStat entryStat = entries.get(entryKey);
            if (entryStat == null) {
                entries.put(entryKey, new ProfileEntryStat());
                entryStat = entries.get(entryKey);
            }
            return entryStat;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Map<String, Object>> getStatData() {

        List<ProfileEntryStatValue> statValueList = getStatValue(false);
        
        int size = statValueList.size();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(size);
        for (ProfileEntryStatValue profileEntryStatValue : statValueList) {
            list.add(profileEntryStatValue.getData());
        }

        return list;
    }

    public List<ProfileEntryStatValue> getStatValue(boolean reset) {
        List<ProfileEntryStatValue> list = new ArrayList<ProfileEntryStatValue>();

        lock.readLock().lock();
        try {
            for (Map.Entry<ProfileEntryKey, ProfileEntryStat> entry : entries.entrySet()) {
                ProfileEntryStatValue entryStatValue = entry.getValue().getValue(reset);
                entry.getKey().fillValue(entryStatValue);

                list.add(entryStatValue);
            }
        } finally {
            lock.readLock().unlock();
        }

        Collections.reverse(list);

        return list;
    }

}
