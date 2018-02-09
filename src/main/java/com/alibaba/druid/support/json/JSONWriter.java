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
package com.alibaba.druid.support.json;

import com.alibaba.druid.sql.visitor.SQLEvalVisitor;
import com.alibaba.druid.util.Utils;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class JSONWriter {

    private StringBuilder    out;



    public JSONWriter(){
        this.out = new StringBuilder();
    }

    public void writeArrayStart() {
        write('[');
    }

    public void writeComma() {
        write(',');
    }

    public void writeArrayEnd() {
        write(']');
    }
    
    public void writeNull() {
        write("null");
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void writeObject(Object o) {
        if (o == null) {
            writeNull();
            return;
        }

        if (o instanceof String) {
            writeString((String) o);
            return;
        }

        if (o instanceof Number) {
            write(o.toString());
            return;
        }

        if (o instanceof Boolean) {
            write(o.toString());
            return;
        }

        if (o instanceof Date) {
            writeDate((Date) o);
            return;
        }

        if (o instanceof Collection) {
            writeArray((Collection) o);
            return;
        }

        if (o instanceof Throwable) {
            writeError((Throwable) o);
            return;
        }

        if (o instanceof int[]) {
            int[] array = (int[]) o;
            write('[');
            for (int i = 0; i < array.length; ++i) {
                if (i != 0) {
                    write(',');
                }
                write(array[i]);
            }
            write(']');
            return;
        }

        if (o instanceof long[]) {
            long[] array = (long[]) o;
            write('[');
            for (int i = 0; i < array.length; ++i) {
                if (i != 0) {
                    write(',');
                }
                write(array[i]);
            }
            write(']');
            return;
        }

        if (o instanceof TabularData) {
            writeTabularData((TabularData) o);
            return;
        }

        if (o instanceof CompositeData) {
            writeCompositeData((CompositeData) o);
            return;
        }

        if (o instanceof Map) {
            writeMap((Map) o);
            return;
        }
        
        if (o == SQLEvalVisitor.EVAL_VALUE_NULL) {
            write("null");
            return;
        }

        throw new IllegalArgumentException("not support type : " + o.getClass());
    }

    public void writeDate(Date date) {
        if (date == null) {
            writeNull();
            return;
        }
        //SimpleDataFormat is not thread-safe, we need to make it local.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        writeString(dateFormat.format(date));
    }

    public void writeError(Throwable error) {
        if (error == null) {
            writeNull();
            return;
        }

        write("{\"Class\":");
        writeString(error.getClass().getName());
        write(",\"Message\":");
        writeString(error.getMessage());
        write(",\"StackTrace\":");
        writeString(Utils.getStackTrace(error));
        write('}');
    }

    public void writeArray(Object[] array) {
        if (array == null) {
            writeNull();
            return;
        }

        write('[');

        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                write(',');
            }
            writeObject(array[i]);
        }

        write(']');
    }

    public void writeArray(Collection<Object> list) {
        if (list == null) {
            writeNull();
            return;
        }

        int entryIndex = 0;
        write('[');

        for (Object entry : list) {
            if (entryIndex != 0) {
                write(',');
            }
            writeObject(entry);
            entryIndex++;
        }

        write(']');
    }

    public void writeString(String text) {
        if (text == null) {
            writeNull();
            return;
        }
        
        write('"');
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (c == '"') {
                write("\\\"");
            } else if (c == '\n') {
                write("\\n");
            } else if (c == '\r') {
                write("\\r");
            } else if (c == '\\') {
                write("\\\\");
            } else if (c == '\t') {
                write("\\t");
            } else if (c < 16) {
                write("\\u000");
                write(Integer.toHexString(c));
            } else if (c < 32) {
                write("\\u00");
                write(Integer.toHexString(c));
            } else if (c >= 0x7f && c <= 0xA0) {
                write("\\u00");
                write(Integer.toHexString(c));
            } else {
                write(c);
            }
        }
        write('"');
    }

    public void writeTabularData(TabularData tabularData) {
        if (tabularData == null) {
            writeNull();
            return;
        }

        int entryIndex = 0;
        write('[');

        for (Object item : tabularData.values()) {
            if (entryIndex != 0) {
                write(',');
            }
            CompositeData row = (CompositeData) item;
            writeCompositeData(row);

            entryIndex++;
        }
        write(']');
    }

    public void writeCompositeData(CompositeData compositeData) {
        if (compositeData == null) {
            writeNull();
            return;
        }

        int entryIndex = 0;
        write('{');

        for (Object key : compositeData.getCompositeType().keySet()) {
            if (entryIndex != 0) {
                write(',');
            }
            writeString((String) key);
            write(':');
            Object value = compositeData.get((String) key);
            writeObject(value);
            entryIndex++;
        }

        write('}');
    }

    public void writeMap(Map<String, Object> map) {
        if (map == null) {
            writeNull();
            return;
        }

        int entryIndex = 0;
        write('{');
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entryIndex != 0) {
                write(',');
            }

            writeString(entry.getKey());
            write(':');
            writeObject(entry.getValue());

            entryIndex++;
        }

        write('}');
    }

    protected void write(String text) {
        out.append(text);
    }

    protected void write(char c) {
        out.append(c);
    }

    protected void write(int c) {
        out.append(c);
    }

    protected void write(long c) {
        out.append(c);
    }

    public String toString() {
        return out.toString();
    }
}
