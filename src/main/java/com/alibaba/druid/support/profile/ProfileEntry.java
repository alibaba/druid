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
