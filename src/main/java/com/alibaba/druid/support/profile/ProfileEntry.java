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

public class ProfileEntry {

    private final ProfileEntry    parent;
    private final ProfileEntryKey key;

    private int                   executeCount     = 0;
    private long                  executeTimeNanos = 0;

    public ProfileEntry(ProfileEntry parent, ProfileEntryKey key){
        this.parent = parent;
        this.key = key;
    }

    public ProfileEntry getParent() {
        return parent;
    }

    public ProfileEntryKey getKey() {
        return key;
    }

    public String getParentName() {
        return key.getParentName();
    }

    public String getName() {
        return key.getName();
    }

    public String getType() {
        return key.getType();
    }

    public int getExecuteCount() {
        return executeCount;
    }

    public void incrementExecuteCount() {
        this.executeCount++;
    }

    public long getExecuteTimeNanos() {
        return executeTimeNanos;
    }

    public void addExecuteTimeNanos(long nanos) {
        this.executeTimeNanos += nanos;
    }

}
