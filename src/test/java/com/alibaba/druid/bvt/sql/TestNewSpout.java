package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.parser.SymbolTable;
import com.alibaba.druid.util.FnvHash;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yunning on 16/7/10.
 */
public class TestNewSpout {


    public static int indexOf_nsharp(byte[] bytes, int fromIndex) {
        int end = bytes.length - 2;
        for (int i = fromIndex; i < end; ++i) {
            if (bytes[i] == '\n'
                    && bytes[i + 1] == '#') {
                return i;
            }
        }

        return -1;
    }

    public static int indexOf_nReturn(byte[] bytes, int fromIndex) {
        for (int i = fromIndex; i < bytes.length; ++i) {
            if (bytes[i] == '\n') {
                return i;
            }
        }

        return -1;
    }

    public static int indexOfTime(byte[] bytes, int fromIndex) {
        int end = bytes.length - 5;
        for (int i = fromIndex; i < end; ++i) {
            if (bytes[i] == '#'
                    && bytes[i + 1] == 't'
                    && bytes[i + 2] == 'i'
                    && bytes[i + 3] == 'm'
                    && bytes[i + 4] == 'e') {
                return i;
            }
        }

        return -1;
    }

    public static final SymbolTable dbSymbols = new SymbolTable(1024 * 256);

    public static String substring(byte[] bytes, int from, int to) {
        int newLength = to - from;
        byte[] copy = new byte[newLength];
        System.arraycopy(bytes, from, copy, 0, newLength);
        return new String(copy, Charset.forName("UTF-8"));
    }

    public static void main(String[] args) throws Exception {
        final String datass = "#time:1470191976686205\n" +
                "#user@host:monitor[monitor] @ [127.0.0.1]\n" +
                "#db:test\n" +
                "#table_name:aa\n" +
                "set session time_zone='+08:00';\n" +
                "#query_time:0.000028123456789987654321\n" +
                "#lock_time:0.000000\n" +
                "#rows_sent:0\n" +
                "#rows_examined:0\n" +
                "#rows_affected:0\n" +
                "#innodb_pages_read:1\n" +
                "#innodb_pages_io_read:2\n" +
                "#id:55438\n" +
                "#time:1470191976686207\n" +
                "#user@host:monitor[monitor] @ [127.0.0.1]\n" +
                "#db:test\n" +
                "#table_name:aa\n" +
                "#time:1470191976686206\n" +
                "#user@host:monitor[monitor] @ [127.0.0.1]\n" +
                "#db:test\n" +
                "#table_name:aa\n" +
                "set session time_zone='+0800';\n" +
                "#query_time:0.000028123456789987654322\n" +
                "#lock_time:0.000000\n" +
                "#rows_sent:0\n" +
                "#rows_examined:0\n" +
                "#rows_affected:0\n" +
                "#innodb_pages_read:0\n" +
                "#innodb_pages_io_read:0\n" +
                "#id:123";

        byte[] datas = datass.getBytes();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            final int len = datas.length;
            int offset = indexOfTime(datas, 0);
            long now = System.nanoTime();
            while (offset != -1) {
                int rowOffset = offset;
                int k = 0;
                final int[] offsets = new int[12];
                boolean isNoFirst = false;
                while (rowOffset != -1 && k < 12) {
                    if (isNoFirst && rowOffset + 5 <= len - 1) {
                        if (datas[rowOffset + 2] == 't' &&
                                datas[rowOffset + 3] == 'i' &&
                                datas[rowOffset + 4] == 'm' &&
                                datas[rowOffset + 5] == 'e') {
                            break;
                        }
                    }
                    offsets[k] = rowOffset;
                    rowOffset = indexOf_nsharp(datas, rowOffset + 1);
                    k = k + 1;
                    isNoFirst = true;
                }
                offset = rowOffset == -1 ? -1 : indexOfTime(datas, rowOffset + 1);
                if (k < 12) {
                    continue;
                }
                SqlInfo sqlInfo = new SqlInfo();
                //  sqlInfo.setHostName(hostName);

                boolean isError = false;
                String sql = null;

                //time
                int currItemOffset = offsets[0];
                int nextItemOffset = offsets[1];
                           /* String item = datas.substring(currItemOffset + 6, nextItemOffset);
                            long itemTime = Long.parseLong(item);*/
                long itemTime = MathUtil.str2Long(datas, currItemOffset + 6, nextItemOffset);
                System.out.println("TIME : " + itemTime);
                if (itemTime < 0 || itemTime > 2000000000000000L) {
                }
                sqlInfo.setTime(itemTime);

                //user@host
                currItemOffset = offsets[1];
                nextItemOffset = offsets[2];
                String item = substring(datas, currItemOffset + 12, nextItemOffset);
                System.out.println("user@host : " + item);
//                sqlInfo.setUserHost(item);

                //db
                currItemOffset = offsets[2];
                nextItemOffset = offsets[3];
                int dbOffset = currItemOffset + 5;
                int dbLen = nextItemOffset - dbOffset;
                long dbHash = FnvHash.fnv1a_64(datas, dbOffset, nextItemOffset);
                String db = dbSymbols.addSymbol(datas, dbOffset, dbLen, dbHash);

                //  String db = substring(datas, currItemOffset + 5, nextItemOffset);
                System.out.println("db : " + db);
                if (dbLen > 0 && SqlUtil.filterDbs.indexOf(db) != -1) {
//                    sqlInfo.setNotValiDB(true);
                } else {
                }

                //table_name
                currItemOffset = offsets[3];
                nextItemOffset = offsets[4];
                int tsqlPos = currItemOffset + 13;
                int idx2 = indexOf_nReturn(datas, tsqlPos);
                String table = substring(datas, tsqlPos, idx2);
                sql = substring(datas, idx2 + 1, nextItemOffset);
                System.out.println("table : " + table + " : " + sql);

                int totalLen = sql.length();
                if (totalLen < 9 ||
                        totalLen >= 8000) {
//                    sqlInfo.setParseRes(0);
                } else {
                }

                //query_time
                currItemOffset = offsets[4];
                nextItemOffset = offsets[5];
                int rtBound = Math.min(nextItemOffset - currItemOffset, 24);
                double itemRt = MathUtil.str2Double(datas, currItemOffset + 13, currItemOffset + rtBound);
                sqlInfo.setQueryTime(itemRt);

                //lock_time
                currItemOffset = offsets[5];
                nextItemOffset = offsets[6];
                int lockTimeBound = Math.min(nextItemOffset - currItemOffset, 23);
                double itemLockTime = MathUtil.str2Double(datas, currItemOffset + 12, currItemOffset + lockTimeBound);
                sqlInfo.setLockTime(itemLockTime);

                //rows_sent
                currItemOffset = offsets[6];
                nextItemOffset = offsets[7];
                long itemRowsSent = MathUtil.str2Long(datas, currItemOffset + 12, nextItemOffset);
                if (itemRowsSent < 0 || itemRowsSent > 1000000000) {
                }
//                sqlInfo.setRowsSent(itemRowsSent);

                //rows_examined
                currItemOffset = offsets[7];
                nextItemOffset = offsets[8];
                long itemRowsExam = MathUtil.str2Long(datas, currItemOffset + 16, nextItemOffset);
                if (itemRowsExam < 0 || itemRowsExam > 1000000000) {
                }
//                sqlInfo.setRowsExamined(itemRowsExam);

                //rows_affected
                currItemOffset = offsets[8];
                nextItemOffset = offsets[9];
                long itemRowsAffected = MathUtil.str2Long(datas, currItemOffset + 16, nextItemOffset);
                if (itemRowsAffected < 0 || itemRowsAffected > 1000000000) {
                }
                sqlInfo.setRowsAffected(itemRowsAffected);

                //innodb_pages_read
                currItemOffset = offsets[9];
                nextItemOffset = offsets[10];
                long itemPageRead = MathUtil.str2Long(datas, currItemOffset + 20, nextItemOffset);
                if (itemPageRead < 0 || itemPageRead > 1000000000) {
                }
                sqlInfo.setInnodbPagesRead(itemPageRead);

                //innodb_pages_io_read
                currItemOffset = offsets[10];
                nextItemOffset = offsets[11];
                long itemPageIORead = MathUtil.str2Long(datas, currItemOffset + 23, nextItemOffset);
                if (itemPageIORead < 0 || itemPageIORead > 1000000000) {
                }
//                sqlInfo.setInnodbPagesIoRead(itemPageIORead);
                // currItemOffset = nextItemOffset;

                            /*nextItemOffset = datas.indexOf("\n#", currItemOffset + 1);
                            if (nextItemOffset >= end) {
                                return null;
                            }
                            itemEnd = nextItemOffset == -1 ? end : nextItemOffset;
                            item = datas.substring(currItemOffset + 1, itemEnd);
                            sqlInfo.setId(Long.parseLong(item.trim().substring(4)));
                            num++;*/
                // sqlInfo.setId(0);
            }

        }

        System.out.println(System.currentTimeMillis() - start);
    }

    static class SqlInfo {
        private long innodbPagesRead;
        private long time;
        private double queryTime;
        private double lockTime;
        public long rowsAffected;

        public long getInnodbPagesRead() {
            return innodbPagesRead;
        }

        public void setInnodbPagesRead(long innodbPagesRead) {
            this.innodbPagesRead = innodbPagesRead;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public void setQueryTime(double queryTime) {
            this.queryTime = queryTime;
        }

        public void setLockTime(double lockTime) {
            this.lockTime = lockTime;
        }

        public void setRowsAffected(long rowsAffected) {
            this.rowsAffected = rowsAffected;
        }
    }

    static class MathUtil {
        public static long str2Long(byte[] data, int start, int end) {
            long value = 0;
            for (int i = start; i < end; ++i) {
                byte ch = data[i];
                if (ch == '.') break;
                int digit = data[i] - '0';
                value = value * 10 + digit;
            }
            return value;
        }

        public static double str2Double(byte[] data,int start,int end) {
            long value = 0;
            long power = 0;
            for (int i = start; i < end; ++i) {
                byte ch = data[i];
                if (ch == '.') {
                    power = 1;
                    continue;
                }
                int digit = ch - '0';
                value = value * 10 + digit;
                power *= 10;
            }
            double doubleValue = (double) value;
            if (power == 0) {
                return doubleValue;
            } else {
                return doubleValue / power;
            }
        }
    }

    static class SqlUtil {

        public static Set<String> filterDb = new HashSet<String>();

        private static int[] orders = new int[]{2, 3, 0, 1};

        static {
            filterDb.add("DRC");
            filterDb.add("INFORMATION_SCHEMA");
            filterDb.add("INNODB_MEMCACHE");
            filterDb.add("MYSQL");
            filterDb.add("PERFORMANCE_SCHEMA");
            filterDb.add("RECYCLE_BIN");
            filterDb.add("ROCKSDB");
            filterDb.add("TEST");
            filterDb.add("DRC".toLowerCase());
            filterDb.add("INFORMATION_SCHEMA".toLowerCase());
            filterDb.add("INNODB_MEMCACHE".toLowerCase());
            filterDb.add("MYSQL".toLowerCase());
            filterDb.add("PERFORMANCE_SCHEMA".toLowerCase());
            filterDb.add("RECYCLE_BIN".toLowerCase());
            filterDb.add("ROCKSDB".toLowerCase());
            filterDb.add("TEST".toLowerCase());
        }

        public static final String filterDbs = org.apache.commons.lang3.StringUtils.join(SqlUtil.filterDb, ",");
    }
}