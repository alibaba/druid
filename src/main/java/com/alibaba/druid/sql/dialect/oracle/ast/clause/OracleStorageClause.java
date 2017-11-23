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
package com.alibaba.druid.sql.dialect.oracle.ast.clause;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleStorageClause extends OracleSQLObjectImpl {

    private SQLExpr        initial;
    private SQLExpr        next;
    private SQLExpr        minExtents;
    private SQLExpr        maxExtents;
    private SQLExpr        maxSize;
    private SQLExpr        pctIncrease;
    private SQLExpr        freeLists;
    private SQLExpr        freeListGroups;
    private SQLExpr        bufferPool;
    private SQLExpr        objno;
    private FlashCacheType flashCache;
    private FlashCacheType cellFlashCache;

    public OracleStorageClause clone() {
        OracleStorageClause x = new OracleStorageClause();
        if (initial != null) {
            x.setInitial(initial.clone());
        }
        if (next != null) {
            x.setNext(next.clone());
        }
        if (minExtents != null) {
            x.setMinExtents(minExtents.clone());
        }
        if (maxExtents != null) {
            x.setMinExtents(maxExtents.clone());
        }
        if (maxSize != null) {
            x.setMaxSize(maxSize.clone());
        }
        if (pctIncrease != null) {
            x.setPctIncrease(pctIncrease.clone());
        }
        if (freeLists != null) {
            x.setFreeLists(freeLists.clone());
        }
        if (freeListGroups != null) {
            x.setFreeListGroups(freeListGroups.clone());
        }
        if (bufferPool != null) {
            x.setBufferPool(bufferPool.clone());
        }
        if (objno != null) {
            x.setObjno(objno.clone());
        }
        x.flashCache = flashCache;
        x.cellFlashCache = cellFlashCache;
        return x;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, initial);
            acceptChild(visitor, next);
            acceptChild(visitor, minExtents);
            acceptChild(visitor, maxExtents);
            acceptChild(visitor, maxSize);
            acceptChild(visitor, pctIncrease);
            acceptChild(visitor, freeLists);
            acceptChild(visitor, freeListGroups);
            acceptChild(visitor, bufferPool);
            acceptChild(visitor, objno);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(SQLExpr maxSize) {
        this.maxSize = maxSize;
    }

    public FlashCacheType getFlashCache() {
        return flashCache;
    }

    public void setFlashCache(FlashCacheType flashCache) {
        this.flashCache = flashCache;
    }

    public FlashCacheType getCellFlashCache() {
        return cellFlashCache;
    }

    public void setCellFlashCache(FlashCacheType cellFlashCache) {
        this.cellFlashCache = cellFlashCache;
    }

    public SQLExpr getPctIncrease() {
        return pctIncrease;
    }

    public void setPctIncrease(SQLExpr pctIncrease) {
        this.pctIncrease = pctIncrease;
    }

    public SQLExpr getNext() {
        return next;
    }

    public void setNext(SQLExpr next) {
        this.next = next;
    }

    public SQLExpr getMinExtents() {
        return minExtents;
    }

    public void setMinExtents(SQLExpr minExtents) {
        this.minExtents = minExtents;
    }

    public SQLExpr getMaxExtents() {
        return maxExtents;
    }

    public void setMaxExtents(SQLExpr maxExtents) {
        this.maxExtents = maxExtents;
    }

    public SQLExpr getObjno() {
        return objno;
    }

    public void setObjno(SQLExpr objno) {
        this.objno = objno;
    }

    public SQLExpr getInitial() {
        return initial;
    }

    public void setInitial(SQLExpr initial) {
        this.initial = initial;
    }

    public SQLExpr getFreeLists() {
        return freeLists;
    }

    public void setFreeLists(SQLExpr freeLists) {
        this.freeLists = freeLists;
    }

    public SQLExpr getFreeListGroups() {
        return freeListGroups;
    }

    public void setFreeListGroups(SQLExpr freeListGroups) {
        this.freeListGroups = freeListGroups;
    }

    public SQLExpr getBufferPool() {
        return bufferPool;
    }

    public void setBufferPool(SQLExpr bufferPool) {
        this.bufferPool = bufferPool;
    }

    public static enum FlashCacheType {
        KEEP, NONE, DEFAULT
    }


}
