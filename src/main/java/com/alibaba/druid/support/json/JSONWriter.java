package com.alibaba.druid.support.json;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import com.alibaba.druid.util.IOUtils;

public class JSONWriter {

    private StringBuilder    out;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public JSONWriter(){
        this.out = new StringBuilder();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void writeObject(Object o) {
        if (o == null) {
            write("null");
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

        throw new IllegalArgumentException("not support type : " + o.getClass());
    }

    public void writeDate(Date date) {
        if (date == null) {
            write("null");
            return;
        }

        writeString(dateFormat.format(date));
    }

    public void writeError(Throwable error) {
        if (error == null) {
            write("null");
            return;
        }

        write("{\"Class\":");
        writeString(error.getClass().getName());
        write(",\"Message\":");
        writeString(error.getMessage());
        write(",\"StackTrace\":");
        writeString(IOUtils.getStackTrace(error));
        write('}');
    }

    public void writeArray(Object[] array) {
        if (array == null) {
            write("null");
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
            write("null");
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
            } else {
                write(c);
            }
        }
        write('"');
    }

    public void writeTabularData(TabularData tabularData) {
        if (tabularData == null) {
            write("null");
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
            write("null");
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
            write("null");
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
