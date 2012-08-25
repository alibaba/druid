/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.Manifest;

public final class VERSION {

    public static String getVersionNumber() {
        return getManifestInfo("Implementation-Version");
    }

    public static String getBuildTime() {
        return getManifestInfo("Implementation-Build");
    }

    @SuppressWarnings("rawtypes")
    private static String getManifestInfo(String key) {
        try {
            URL res = VERSION.class.getResource(VERSION.class.getSimpleName() + ".class");
            JarURLConnection conn = (JarURLConnection) res.openConnection();
            Manifest manifest = conn.getManifest();
            Iterator entries = manifest.getMainAttributes().entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                String manifestKey = entry.getKey().toString();
                if (key.equals(manifestKey)) {
                    return (String) entry.getValue();
                }
            }
            return null;
        } catch (IOException e) {
            return "n/a";
        }
    }
}
