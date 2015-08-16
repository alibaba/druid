/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.support.monitor;

import com.alibaba.druid.support.http.ResourceServlet;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
public class MonitorServlet extends ResourceServlet {

    private String      mappingPath = "support/http/resources";
    private Set<String> mapping     = new HashSet<String>();

    public MonitorServlet(){
        super("support/monitor/resources");

        mapping.add("/css/bootstrap.min.css");
        mapping.add("/js/bootstrap.min.js");
        mapping.add("/js/jquery.min.js");
    }

    protected String getFilePath(String fileName) {
        if (mapping.contains(fileName)) {
            return mappingPath + fileName;
        }

        return super.getFilePath(fileName);
    }

    @Override
    protected String process(String url) {
        // data.json?type=dataSource
        // data.json?type=sql
        // data.json?type=webapp
        // data.json?type=weburi

        return null;
    }
}
