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
package com.alibaba.druid.support.json;

public class JSONUtils {

    public static String toJSONString(Object o) {
        JSONWriter writer = new JSONWriter();
        writer.writeObject(o);
        return writer.toString();
    }
    
    public static Object parse(String text) {
        JSONParser parser = new JSONParser(text);
        return parser.parse();
    }
}
