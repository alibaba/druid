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
package com.alibaba.druid.util;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

public class Utils {

    public final static int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static String read(InputStream in) {
        InputStreamReader reader;
        try {
            reader = new InputStreamReader(in, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return read(reader);
    }

    public static String readFromResource(String resource) throws IOException {
        InputStream in = null;
        try {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
            if (in == null) {
                in = Utils.class.getResourceAsStream(resource);
            }

            if (in == null) {
                return null;
            }

            String text = Utils.read(in);
            return text;
        } finally {
            JdbcUtils.close(in);
        }
    }

    public static byte[] readByteArrayFromResource(String resource) throws IOException {
        InputStream in = null;
        try {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
            if (in == null) {
                return null;
            }

            return readByteArray(in);
        } finally {
            JdbcUtils.close(in);
        }
    }

    public static byte[] readByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    public static long copy(InputStream input, OutputStream output) throws IOException {
        final int EOF = -1;

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        long count = 0;
        int n = 0;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static String read(Reader reader) {
        try {

            StringWriter writer = new StringWriter();

            char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            int n = 0;
            while (-1 != (n = reader.read(buffer))) {
                writer.write(buffer, 0, n);
            }

            return writer.toString();
        } catch (IOException ex) {
            throw new IllegalStateException("read error", ex);
        }
    }

    public static String read(Reader reader, int length) {
        try {
            char[] buffer = new char[length];

            int offset = 0;
            int rest = length;
            int len;
            while ((len = reader.read(buffer, offset, rest)) != -1) {
                rest -= len;
                offset += len;

                if (rest == 0) {
                    break;
                }
            }

            return new String(buffer, 0, length - rest);
        } catch (IOException ex) {
            throw new IllegalStateException("read error", ex);
        }
    }

    public static String toString(java.util.Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public static String getStackTrace(Throwable ex) {
        StringWriter buf = new StringWriter();
        ex.printStackTrace(new PrintWriter(buf));

        return buf.toString();
    }

    public static String toString(StackTraceElement[] stackTrace) {
        StringBuilder buf = new StringBuilder();
        for (StackTraceElement item : stackTrace) {
            buf.append(item.toString());
            buf.append("\n");
        }
        return buf.toString();
    }

    public static Boolean getBoolean(Properties properties, String key) {
        String property = properties.getProperty(key);
        if ("true".equals(property)) {
            return Boolean.TRUE;
        } else if ("false".equals(property)) {
            return Boolean.FALSE;
        }
        return null;
    }

    public static Integer getInteger(Properties properties, String key) {
        String property = properties.getProperty(key);

        if (property == null) {
            return null;
        }
        try {
            return Integer.parseInt(property);
        } catch (NumberFormatException ex) {
            // skip
        }
        return null;
    }

    public static Long getLong(Properties properties, String key) {
        String property = properties.getProperty(key);

        if (property == null) {
            return null;
        }
        try {
            return Long.parseLong(property);
        } catch (NumberFormatException ex) {
            // skip
        }
        return null;
    }

    public static Class<?> loadClass(String className) {
        Class<?> clazz = null;

        if (className == null) {
            return null;
        }

        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            // skip
        }

        ClassLoader ctxClassLoader = Thread.currentThread().getContextClassLoader();
        if (ctxClassLoader != null) {
            try {
                clazz = ctxClassLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                // skip
            }
        }

        return clazz;
    }

    private static Date startTime;

    public final static Date getStartTime() {
        if (startTime == null) {
            startTime = new Date(ManagementFactory.getRuntimeMXBean().getStartTime());
        }
        return startTime;
    }

    public static long murmurhash2_64(String text) {
        final byte[] bytes = text.getBytes();
        return murmurhash2_64(bytes, bytes.length, 0xe17a1465);
    }

    /**
     * murmur hash 2.0, The murmur hash is a relatively fast hash function from http://murmurhash.googlepages.com/ for
     * platforms with efficient multiplication.
     *
     * @author Viliam Holub
     */
public static long murmurhash2_64(final byte[] data, int length, int seed) {
    final long m = 0xc6a4a7935bd1e995L;
    final int r = 47;

    long h = (seed & 0xffffffffl) ^ (length * m);

    int length8 = length / 8;

    for (int i = 0; i < length8; i++) {
        final int i8 = i * 8;
        long k = ((long) data[i8 + 0] & 0xff) //
                + (((long) data[i8 + 1] & 0xff) << 8) //
                + (((long) data[i8 + 2] & 0xff) << 16)//
                + (((long) data[i8 + 3] & 0xff) << 24) //
                + (((long) data[i8 + 4] & 0xff) << 32)//
                + (((long) data[i8 + 5] & 0xff) << 40)//
                + (((long) data[i8 + 6] & 0xff) << 48) //
                + (((long) data[i8 + 7] & 0xff) << 56);

        k *= m;
        k ^= k >>> r;
        k *= m;

        h ^= k;
        h *= m;
    }

    switch (length % 8) {
        case 7:
            h ^= (long) (data[(length & ~7) + 6] & 0xff) << 48;
        case 6:
            h ^= (long) (data[(length & ~7) + 5] & 0xff) << 40;
        case 5:
            h ^= (long) (data[(length & ~7) + 4] & 0xff) << 32;
        case 4:
            h ^= (long) (data[(length & ~7) + 3] & 0xff) << 24;
        case 3:
            h ^= (long) (data[(length & ~7) + 2] & 0xff) << 16;
        case 2:
            h ^= (long) (data[(length & ~7) + 1] & 0xff) << 8;
        case 1:
            h ^= (long) (data[length & ~7] & 0xff);
            h *= m;
    }
    ;

    h ^= h >>> r;
    h *= m;
    h ^= h >>> r;

    return h;
}

    public static byte[] md5Bytes(String text) {
        MessageDigest msgDigest = null;

        try {
            msgDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("System doesn't support MD5 algorithm.");
        }

        msgDigest.update(text.getBytes());

        byte[] bytes = msgDigest.digest();

        return bytes;
    }

    public static String md5(String text) {
        byte[] bytes = md5Bytes(text);
        return HexBin.encode(bytes, false);
    }

    public static void putLong(byte[] b, int off, long val) {
        b[off + 7] = (byte) (val >>> 0);
        b[off + 6] = (byte) (val >>> 8);
        b[off + 5] = (byte) (val >>> 16);
        b[off + 4] = (byte) (val >>> 24);
        b[off + 3] = (byte) (val >>> 32);
        b[off + 2] = (byte) (val >>> 40);
        b[off + 1] = (byte) (val >>> 48);
        b[off + 0] = (byte) (val >>> 56);
    }

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static String hex(int hash) {
        byte[] bytes = new byte[4];

        bytes[3] = (byte) (hash       );
        bytes[2] = (byte) (hash >>>  8);
        bytes[1] = (byte) (hash >>> 16);
        bytes[0] = (byte) (hash >>> 24);


        char[] chars = new char[8];
        for (int i = 0; i < 4; ++i) {
            byte b = bytes[i];

            int a = b & 0xFF;
            int b0 = a >> 4;
            int b1 = a & 0xf;

            chars[i * 2] = (char) (b0 + (b0 < 10 ? 48 : 55));
            chars[i * 2 + 1] = (char) (b1 + (b1 < 10 ? 48 : 55));
        }

        return new String(chars);
    }

    public static String hex(long hash) {
        byte[] bytes = new byte[8];

        bytes[7] = (byte) (hash       );
        bytes[6] = (byte) (hash >>>  8);
        bytes[5] = (byte) (hash >>> 16);
        bytes[4] = (byte) (hash >>> 24);
        bytes[3] = (byte) (hash >>> 32);
        bytes[2] = (byte) (hash >>> 40);
        bytes[1] = (byte) (hash >>> 48);
        bytes[0] = (byte) (hash >>> 56);

        char[] chars = new char[16];
        for (int i = 0; i < 8; ++i) {
            byte b = bytes[i];

            int a = b & 0xFF;
            int b0 = a >> 4;
            int b1 = a & 0xf;

            chars[i * 2] = (char) (b0 + (b0 < 10 ? 48 : 55));
            chars[i * 2 + 1] = (char) (b1 + (b1 < 10 ? 48 : 55));
        }

        return new String(chars);
    }

    public static String hex_t(long hash) {
        byte[] bytes = new byte[8];

        bytes[7] = (byte) (hash       );
        bytes[6] = (byte) (hash >>>  8);
        bytes[5] = (byte) (hash >>> 16);
        bytes[4] = (byte) (hash >>> 24);
        bytes[3] = (byte) (hash >>> 32);
        bytes[2] = (byte) (hash >>> 40);
        bytes[1] = (byte) (hash >>> 48);
        bytes[0] = (byte) (hash >>> 56);

        char[] chars = new char[18];
        chars[0] = 'T';
        chars[1] = '_';
        for (int i = 0; i < 8; ++i) {
            byte b = bytes[i];

            int a = b & 0xFF;
            int b0 = a >> 4;
            int b1 = a & 0xf;

            chars[i * 2 + 2] = (char) (b0 + (b0 < 10 ? 48 : 55));
            chars[i * 2 + 3] = (char) (b1 + (b1 < 10 ? 48 : 55));
        }

        return new String(chars);
    }

    public static long fnv_64(String input) {
        return FnvHash.fnv1a_64(input);
    }

    public static long fnv_64_lower(String key) {
        return FnvHash.fnv1a_64_lower(key);
    }

    public static long fnv_32_lower(String key) {
        return FnvHash.fnv_32_lower(key);
    }

    public static void loadFromFile(String path, Set<String> set) {
        InputStream is = null;
        BufferedReader reader = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            reader = new BufferedReader(new InputStreamReader(is));
            for (; ; ) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                line = line.trim().toLowerCase();

                if (line.length() == 0) {
                    continue;
                }
                set.add(line);
            }
        } catch (Exception ex) {
            // skip
        } finally {
            JdbcUtils.close(is);
            JdbcUtils.close(reader);
        }
    }

}
