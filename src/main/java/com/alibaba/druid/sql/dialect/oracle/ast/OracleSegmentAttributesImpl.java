/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.oracle.ast;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLObjectImpl;

/**
 * Created by wenshao on 21/05/2017.
 */
public abstract class OracleSegmentAttributesImpl extends SQLObjectImpl implements OracleSegmentAttributes {
    private Integer pctfree;
    private Integer pctused;
    private Integer initrans;

    private Integer maxtrans;
    private Integer pctincrease;
    private Integer freeLists;
    private Boolean compress;
    private Integer compressLevel;
    private boolean compressForOltp;
    private Integer pctthreshold;

    private Boolean logging;

    protected SQLName tablespace;
    protected SQLObject storage;

    public SQLName getTablespace() {
        return tablespace;
    }

    public void setTablespace(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.tablespace = x;
    }

    public Boolean getCompress() {
        return compress;
    }

    public void setCompress(Boolean compress) {
        this.compress = compress;
    }

    public Integer getCompressLevel() {
        return compressLevel;
    }

    public void setCompressLevel(Integer compressLevel) {
        this.compressLevel = compressLevel;
    }

    public Integer getPctthreshold() {
        return pctthreshold;
    }

    public void setPctthreshold(Integer pctthreshold) {
        this.pctthreshold = pctthreshold;
    }

    public Integer getPctfree() {
        return pctfree;
    }

    public void setPctfree(Integer ptcfree) {
        this.pctfree = ptcfree;
    }

    public Integer getPctused() {
        return pctused;
    }

    public void setPctused(Integer ptcused) {
        this.pctused = ptcused;
    }

    public Integer getInitrans() {
        return initrans;
    }

    public void setInitrans(Integer initrans) {
        this.initrans = initrans;
    }

    public Integer getMaxtrans() {
        return maxtrans;
    }

    public void setMaxtrans(Integer maxtrans) {
        this.maxtrans = maxtrans;
    }

    public Integer getPctincrease() {
        return pctincrease;
    }

    public void setPctincrease(Integer pctincrease) {
        this.pctincrease = pctincrease;
    }

    public Integer getFreeLists() {
        return freeLists;
    }

    public void setFreeLists(Integer freeLists) {
        this.freeLists = freeLists;
    }

    public Boolean getLogging() {
        return logging;
    }

    public void setLogging(Boolean logging) {
        this.logging = logging;
    }

    public SQLObject getStorage() {
        return storage;
    }

    public void setStorage(SQLObject storage) {
        this.storage = storage;
    }

    public boolean isCompressForOltp() {
        return compressForOltp;
    }

    public void setCompressForOltp(boolean compressForOltp) {
        this.compressForOltp = compressForOltp;
    }

    public void cloneTo(OracleSegmentAttributesImpl x) {
        x.pctfree = pctfree;
        x.pctused = pctused;
        x.initrans = initrans;

        x.maxtrans = maxtrans;
        x.pctincrease = pctincrease;
        x.freeLists = freeLists;
        x.compress = compress;
        x.compressLevel = compressLevel;
        x.compressForOltp = compressForOltp;
        x.pctthreshold = pctthreshold;

        x.logging = logging;

        if (tablespace != null) {
            x.setTablespace(tablespace.clone());
        }

        if (storage != null) {
            x.setStorage(storage.clone());
        }
    }
}
