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
package com.alibaba.druid.sql.dialect.oracle.ast;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;

/**
 * Created by wenshao on 21/05/2017.
 */
public interface OracleSegmentAttributes extends SQLObject {

    SQLName getTablespace();
    void setTablespace(SQLName name);

    Boolean getCompress();

    void setCompress(Boolean compress);

    Integer getCompressLevel();

    void setCompressLevel(Integer compressLevel);

    Integer getInitrans();
    void setInitrans(Integer initrans);

    Integer getMaxtrans();
    void setMaxtrans(Integer maxtrans);

    Integer getPctincrease();
    void setPctincrease(Integer pctincrease);

    Integer getPctused();
    void setPctused(Integer pctused);

    Integer getPctfree();
    void setPctfree(Integer ptcfree);

    Boolean getLogging();
    void setLogging(Boolean logging);

    SQLObject getStorage();
    void setStorage(SQLObject storage);

    boolean isCompressForOltp();

    void setCompressForOltp(boolean compressForOltp);
}
