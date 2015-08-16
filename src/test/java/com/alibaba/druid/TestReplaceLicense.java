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
package com.alibaba.druid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import junit.framework.TestCase;

import com.alibaba.druid.util.Utils;

public class TestReplaceLicense extends TestCase {

    private String license;
    private String lineSeparator;

    protected void setUp() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("License.txt");
        Reader reader = new InputStreamReader(is);
        license = Utils.read(reader);
        reader.close();
        System.out.println(license);

        lineSeparator = "\n"; // (String) java.security.AccessController.doPrivileged(new
                              // sun.security.action.GetPropertyAction("line.separator"));
    }

    public void test_0() throws Exception {
        File file = new File("/usr/alibaba/workspace/druid");
        listFile(file);

    }

    public void listFile(File file) throws Exception {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                listFile(child);
            }
        } else {
            if (file.getName().endsWith(".java")) {
                listJavaFile(file);
            }
        }
    }

    public void listJavaFile(File file) throws Exception {
        FileInputStream in = new FileInputStream(file);
        InputStreamReader reader = new InputStreamReader(in, "utf-8");
        String content = Utils.read(reader);
        reader.close();

        if (!content.startsWith(license)) {
            String newContent;
            int index = content.indexOf("package ");
            if (index != -1) {
                newContent = license + lineSeparator + content.substring(index);
            } else {
                newContent = license + lineSeparator + content;
            }
            FileOutputStream out = new FileOutputStream(file);
            Writer writer = new OutputStreamWriter(out, "utf-8");
            writer.write(newContent);
            writer.close();
        } else {

        }
    }
}
