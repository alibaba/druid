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
package com.alibaba.druid.mock;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.SQLException;
import java.sql.SQLXML;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

public class MockSQLXML implements SQLXML {

    @Override
    public void free() throws SQLException {

    }

    @Override
    public InputStream getBinaryStream() throws SQLException {

        return null;
    }

    @Override
    public OutputStream setBinaryStream() throws SQLException {

        return null;
    }

    @Override
    public Reader getCharacterStream() throws SQLException {

        return null;
    }

    @Override
    public Writer setCharacterStream() throws SQLException {

        return null;
    }

    @Override
    public String getString() throws SQLException {

        return null;
    }

    @Override
    public void setString(String value) throws SQLException {

    }

    @Override
    public <T extends Source> T getSource(Class<T> sourceClass) throws SQLException {

        return null;
    }

    @Override
    public <T extends Result> T setResult(Class<T> resultClass) throws SQLException {

        return null;
    }

}
