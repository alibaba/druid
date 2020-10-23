package com.alibaba.druid.wall.spi;

import com.alibaba.druid.wall.*;

import java.util.ArrayList;
import java.util.List;

public abstract class WallVisitorBase implements WallVisitor {
    protected final WallConfig config;
    protected final WallProvider provider;
    protected final List<Violation> violations = new ArrayList<Violation>();
    protected boolean sqlModified = false;
    protected boolean sqlEndOfComment = false;
    protected List<WallUpdateCheckItem> updateCheckItems;

    public WallVisitorBase(WallProvider provider) {
        this.config = provider.getConfig();
        this.provider = provider;
    }

    @Override
    public boolean isSqlModified() {
        return sqlModified;
    }

    @Override
    public void setSqlModified(boolean sqlModified) {
        this.sqlModified = sqlModified;
    }

    @Override
    public WallProvider getProvider() {
        return provider;
    }

    @Override
    public WallConfig getConfig() {
        return config;
    }

    public void addViolation(Violation violation) {
        this.violations.add(violation);
    }

    @Override
    public List<Violation> getViolations() {
        return violations;
    }

    @Override
    public boolean isSqlEndOfComment() {
        return this.sqlEndOfComment;
    }

    @Override
    public void setSqlEndOfComment(boolean sqlEndOfComment) {
        this.sqlEndOfComment = sqlEndOfComment;
    }

    public void addWallUpdateCheckItem(WallUpdateCheckItem item) {
        if (updateCheckItems == null) {
            updateCheckItems = new ArrayList<WallUpdateCheckItem>();
        }
        updateCheckItems.add(item);
    }

    public List<WallUpdateCheckItem> getUpdateCheckItems() {
        return updateCheckItems;
    }

    public boolean isDenyTable(String name) {
        if (!config
                .isTableCheck()) {
            return false;
        }

        return !provider.checkDenyTable(name);
    }
}
