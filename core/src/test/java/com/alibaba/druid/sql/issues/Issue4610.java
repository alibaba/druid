package com.alibaba.druid.sql.issues;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

import junit.framework.TestCase;

/**
 * 验证各种类型的参数的格式化sql合法性的用例
 *
 * @author lizongbo 把 com.mysql.cj.NativeQueryBindings.DEFAULT_MYSQL_TYPES 支持的java类型全部都加上参数格式化验证逻辑
 * @see <a href="https://github.com/alibaba/druid/issues/4610">LocalDateTime等类型的输出问题</a>
 */
public class Issue4610 extends TestCase {


    public void test_printParameter_BigDecimal() throws Exception {
        String sql = "update t set bigdecimal_val = ?";
        String sqlNeed = "UPDATE t\nSET bigdecimal_val = 50000000000";
        List<Object> parameters = new ArrayList<>();
        parameters.add(new BigDecimal(Double.toString(50000000000.00)));
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_BigInteger() throws Exception {
        String sql = "update t set biginteger_val = ?"; // MysqlType.BIGINT
        String sqlNeed = "UPDATE t\nSET biginteger_val = 987654321987654321987654321";
        List<Object> parameters = new ArrayList<>();
        parameters.add(new BigInteger("987654321987654321987654321"));
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_Blob() throws Exception {
        String sql = "update t set blob_val = ?"; // MysqlType.BLOB
        String sqlNeed = "UPDATE t\nSET blob_val = '<Blob>'";
        List<Object> parameters = new ArrayList<>();
        parameters.add(new Blob() {
            @Override
            public long length() throws SQLException {
                return 0;
            }

            @Override
            public byte[] getBytes(long pos, int length) throws SQLException {
                return new byte[0];
            }

            @Override
            public InputStream getBinaryStream() throws SQLException {
                return null;
            }

            @Override
            public long position(byte[] pattern, long start) throws SQLException {
                return 0;
            }

            @Override
            public long position(Blob pattern, long start) throws SQLException {
                return 0;
            }

            @Override
            public int setBytes(long pos, byte[] bytes) throws SQLException {
                return 0;
            }

            @Override
            public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
                return 0;
            }

            @Override
            public OutputStream setBinaryStream(long pos) throws SQLException {
                return null;
            }

            @Override
            public void truncate(long len) throws SQLException {

            }

            @Override
            public void free() throws SQLException {

            }

            @Override
            public InputStream getBinaryStream(long pos, long length) throws SQLException {
                return null;
            }
        });
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_Boolean() throws Exception {
        String sql = "update t set boolean_val = ?"; // MysqlType.BOOLEAN
        String sqlNeed = "UPDATE t\nSET boolean_val = true";
        List<Object> parameters = new ArrayList<>();
        parameters.add(Boolean.TRUE);
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_Byte() throws Exception {
        String sql = "update t set byte_val = ?"; // MysqlType.TINYINT
        String sqlNeed = "UPDATE t\nSET byte_val = 32";
        List<Object> parameters = new ArrayList<>();
        parameters.add(new Byte((byte) 32));
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_ByteArray() throws Exception {
        String sql = "update t set bytearr_val = ?"; // MysqlType.BINARY
        String sqlNeed = "UPDATE t\nSET bytearr_val = x'616263'";
        List<Object> parameters = new ArrayList<>();
        parameters.add("abc".getBytes(StandardCharsets.UTF_8));
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_Calendar() throws Exception {
        Calendar c = Calendar.getInstance();
        String sql = "update t set calendar_val = ?"; // MysqlType.TIMESTAMP
        String sqlNeed =
            "UPDATE t\nSET calendar_val = '" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new java.sql.Timestamp(c.getTimeInMillis()))
                + "'";
        List<Object> parameters = new ArrayList<>();
        parameters.add(c);
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_NClob() throws Exception {
        String sql = "update t set clob_val = ?"; // MysqlType.TEXT
        String sqlNeed = "UPDATE t\nSET clob_val = '<NClob>'";
        List<Object> parameters = new ArrayList<>();
        parameters.add(new NClob() {

            @Override
            public long length() throws SQLException {
                return 0;
            }

            @Override
            public String getSubString(long pos, int length) throws SQLException {
                return null;
            }

            @Override
            public Reader getCharacterStream() throws SQLException {
                return null;
            }

            @Override
            public InputStream getAsciiStream() throws SQLException {
                return null;
            }

            @Override
            public long position(String searchstr, long start) throws SQLException {
                return 0;
            }

            @Override
            public long position(Clob searchstr, long start) throws SQLException {
                return 0;
            }

            @Override
            public int setString(long pos, String str) throws SQLException {
                return 0;
            }

            @Override
            public int setString(long pos, String str, int offset, int len) throws SQLException {
                return 0;
            }

            @Override
            public OutputStream setAsciiStream(long pos) throws SQLException {
                return null;
            }

            @Override
            public Writer setCharacterStream(long pos) throws SQLException {
                return null;
            }

            @Override
            public void truncate(long len) throws SQLException {

            }

            @Override
            public void free() throws SQLException {

            }

            @Override
            public Reader getCharacterStream(long pos, long length) throws SQLException {
                return null;
            }
        });
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_Clob() throws Exception {
        String sql = "update t set clob_val = ?"; // MysqlType.TEXT
        String sqlNeed = "UPDATE t\nSET clob_val = '<Clob>'";
        List<Object> parameters = new ArrayList<>();
        parameters.add(new Clob() {

            @Override
            public long length() throws SQLException {
                return 0;
            }

            @Override
            public String getSubString(long pos, int length) throws SQLException {
                return null;
            }

            @Override
            public Reader getCharacterStream() throws SQLException {
                return null;
            }

            @Override
            public InputStream getAsciiStream() throws SQLException {
                return null;
            }

            @Override
            public long position(String searchstr, long start) throws SQLException {
                return 0;
            }

            @Override
            public long position(Clob searchstr, long start) throws SQLException {
                return 0;
            }

            @Override
            public int setString(long pos, String str) throws SQLException {
                return 0;
            }

            @Override
            public int setString(long pos, String str, int offset, int len) throws SQLException {
                return 0;
            }

            @Override
            public OutputStream setAsciiStream(long pos) throws SQLException {
                return null;
            }

            @Override
            public Writer setCharacterStream(long pos) throws SQLException {
                return null;
            }

            @Override
            public void truncate(long len) throws SQLException {

            }

            @Override
            public void free() throws SQLException {

            }

            @Override
            public Reader getCharacterStream(long pos, long length) throws SQLException {
                return null;
            }
        });
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_Date() throws Exception {
        java.sql.Date d = new java.sql.Date(System.currentTimeMillis());
        String sql = "update t set date_val = ?"; // MysqlType.DATE
        String sqlNeed = "UPDATE t\nSET date_val = DATE '" + d + "'";
        List<Object> parameters = new ArrayList<>();
        parameters.add(d);
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_JavaUtilDate() throws Exception {
        java.util.Date d = new java.util.Date();
        String sql = "update t set java_util_date_val = ?"; // MysqlType.TIMESTAMP
        String sqlNeed = "UPDATE t\nSET java_util_date_val = TIMESTAMP '" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(
            new java.sql.Timestamp(d.getTime())) + "'";
        List<Object> parameters = new ArrayList<>();
        parameters.add(d);
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_Double() throws Exception {
        Double d = new Double(50000000000000.01);
        String sql = "update t set double_val = ?"; // MysqlType.DOUBLE
        String sqlNeed = "UPDATE t\nSET double_val = " + new BigDecimal(d).toPlainString();
        List<Object> parameters = new ArrayList<>();
        parameters.add(d);
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_Duration() throws Exception {
        Duration d = Duration.ofHours(3);
        String sql = "update t set duration_val = ?"; // MysqlType.TIME
        String sqlNeed = "UPDATE t\nSET duration_val = '" + d + "'";
        List<Object> parameters = new ArrayList<>();
        parameters.add(d);
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_Float() throws Exception {
        String sql = "update t set float_val = ?"; // MysqlType.FLOAT
        String sqlNeed = "UPDATE t\nSET float_val = 1234.56";
        List<Object> parameters = new ArrayList<>();
        parameters.add(new Float(1234.56F));
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_InputStream() throws Exception {
        String sql = "update t set inputstream_val = ?"; // MysqlType.BLOB
        String sqlNeed = "UPDATE t\nSET inputstream_val = '<InputStream>'";
        List<Object> parameters = new ArrayList<>();
        parameters.add(new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        });
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_Instant() throws Exception {
        Instant i = Instant.ofEpochMilli(System.currentTimeMillis());
        String sql = "update t set instant_val = ?"; // MysqlType.TIMESTAMP
        String sqlNeed = "UPDATE t\nSET instant_val = '" + i + "'";
        List<Object> parameters = new ArrayList<>();
        parameters.add(i);
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_Integer() throws Exception {
        String sql = "update t set integer_val = ?"; // MysqlType.INT
        String sqlNeed = "UPDATE t\nSET integer_val = 20231126";
        List<Object> parameters = new ArrayList<>();
        parameters.add(new Integer(20231126));
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_LocalDate() throws Exception {
        LocalDate ld = LocalDate.now();
        String sql = "update t set localdate_val = ?"; // MysqlType.DATE
        String sqlNeed = "UPDATE t\nSET localdate_val = '" + ld.toString() + "'";
        List<Object> parameters = new ArrayList<>();
        parameters.add(ld);
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_LocalDateTime() throws Exception {
        LocalDateTime ldt = LocalDateTime.now();
        String sql = "update t set localdatetime_val = ?"; // MysqlType.DATETIME  // default JDBC mapping is TIMESTAMP
        String sqlNeed = "UPDATE t\nSET localdatetime_val = '" + ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")) + "'";
        List<Object> parameters = new ArrayList<>();
        parameters.add(ldt);
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_LocalTime() throws Exception {
        LocalTime lt = LocalTime.now();
        String sql = "update t set localtime_val = ?"; // MysqlType.TIME
        String sqlNeed = "UPDATE t\nSET localtime_val = '" + lt.toString() + "'";
        List<Object> parameters = new ArrayList<>();
        parameters.add(lt);
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_Long() throws Exception {
        String sql = "update t set long_val = ?"; // MysqlType.BIGINT
        String sqlNeed = "UPDATE t\nSET long_val = 2023112320231123";
        List<Object> parameters = new ArrayList<>();
        parameters.add(new Long(2023112320231123L));
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_OffsetDateTime() throws Exception {
        OffsetDateTime odt = OffsetDateTime.now();
        String sql = "update t set offsetdatetime_val = ?"; // MysqlType.TIMESTAMP  // default JDBC mapping is TIMESTAMP_WITH_TIMEZONE
        String sqlNeed = "UPDATE t\nSET offsetdatetime_val = '" + odt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")) + "'";
        List<Object> parameters = new ArrayList<>();
        parameters.add(odt);
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_OffsetTime() throws Exception {
        OffsetTime ot = OffsetTime.now();
        String sql = "update t set offsettime_val = ?"; // MysqlType.TIME  // default JDBC mapping is TIME_WITH_TIMEZONE
        String sqlNeed = "UPDATE t\nSET offsettime_val = '" + ot + "'";
        List<Object> parameters = new ArrayList<>();
        parameters.add(ot);
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_Reader() throws Exception {
        String sql = "update t set reader_val = ?"; // MysqlType.TEXT
        String sqlNeed = "UPDATE t\nSET reader_val = '<Reader>'";
        List<Object> parameters = new ArrayList<>();
        parameters.add(new Reader() {
            @Override
            public int read(char[] cbuf, int off, int len) throws IOException {
                return 0;
            }

            @Override
            public void close() throws IOException {

            }
        });
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
        SQLUtils.parseStatements(fromatedSql, DbType.mysql);
    }

    public void test_printParameter_Short() throws Exception {
        String sql = "update t set short_val = ?"; // MysqlType.SMALLINT
        String sqlNeed = "UPDATE t\nSET short_val = 1234";
        List<Object> parameters = new ArrayList<>();
        parameters.add(new Short((short) 1234));
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
    }

    public void test_printParameter_String() throws Exception {
        String sql = "update t set string_val = ?"; // MysqlType.VARCHAR
        String sqlNeed = "UPDATE t\nSET string_val = 'lizongbo'";
        List<Object> parameters = new ArrayList<>();
        parameters.add("lizongbo");
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
    }

    public void test_printParameter_Time() throws Exception {
        java.sql.Time t = new java.sql.Time(System.currentTimeMillis());
        String sql = "update t set time_val = ?"; // MysqlType.TIME
        String sqlNeed = "UPDATE t\nSET time_val = TIME '" + t + "'";
        List<Object> parameters = new ArrayList<>();
        parameters.add(t);
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
    }

    public void test_printParameter_Timestamp() throws Exception {
        java.sql.Timestamp t = new java.sql.Timestamp(System.currentTimeMillis());
        String sql = "update t set timestamp_val = ?"; // MysqlType.TIMESTAMP
        String sqlNeed = "UPDATE t\nSET timestamp_val = TIMESTAMP '" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(t) + "'";
        List<Object> parameters = new ArrayList<>();
        parameters.add(t);
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
    }

    public void test_printParameter_ZonedDateTime() throws Exception {
        ZonedDateTime zdt = ZonedDateTime.now();
        String sql = "update t set zoneddatetime_val = ?"; // MysqlType.TIMESTAMP  // no JDBC mapping is defined
        String sqlNeed = "UPDATE t\nSET zoneddatetime_val = '" + zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")) + "'";
        List<Object> parameters = new ArrayList<>();
        parameters.add(zdt);
        String fromatedSql = SQLUtils.format(sql, DbType.mysql, parameters);
        System.out.println(fromatedSql);
        assertEquals(sqlNeed, fromatedSql);
    }


    /**
     * 来自 com.mysql.cj.NativeQueryBindings.DEFAULT_MYSQL_TYPES
     */
    static String javaCode = "DEFAULT_MYSQL_TYPES.put(BigDecimal.class, MysqlType.DECIMAL);\n"
        + "        DEFAULT_MYSQL_TYPES.put(BigInteger.class, MysqlType.BIGINT);\n"
        + "        DEFAULT_MYSQL_TYPES.put(Blob.class, MysqlType.BLOB);\n"
        + "        DEFAULT_MYSQL_TYPES.put(Boolean.class, MysqlType.BOOLEAN);\n"
        + "        DEFAULT_MYSQL_TYPES.put(Byte.class, MysqlType.TINYINT);\n"
        + "        DEFAULT_MYSQL_TYPES.put(byte[].class, MysqlType.BINARY);\n"
        + "        DEFAULT_MYSQL_TYPES.put(Calendar.class, MysqlType.TIMESTAMP);\n"
        + "        DEFAULT_MYSQL_TYPES.put(Clob.class, MysqlType.TEXT);\n"
        + "        DEFAULT_MYSQL_TYPES.put(Date.class, MysqlType.DATE);\n"
        + "        DEFAULT_MYSQL_TYPES.put(java.util.Date.class, MysqlType.TIMESTAMP);\n"
        + "        DEFAULT_MYSQL_TYPES.put(Double.class, MysqlType.DOUBLE);\n"
        + "        DEFAULT_MYSQL_TYPES.put(Duration.class, MysqlType.TIME);\n"
        + "        DEFAULT_MYSQL_TYPES.put(Float.class, MysqlType.FLOAT);\n"
        + "        DEFAULT_MYSQL_TYPES.put(InputStream.class, MysqlType.BLOB);\n"
        + "        DEFAULT_MYSQL_TYPES.put(Instant.class, MysqlType.TIMESTAMP);\n"
        + "        DEFAULT_MYSQL_TYPES.put(Integer.class, MysqlType.INT);\n"
        + "        DEFAULT_MYSQL_TYPES.put(LocalDate.class, MysqlType.DATE);\n"
        + "        DEFAULT_MYSQL_TYPES.put(LocalDateTime.class, MysqlType.DATETIME); // default JDBC mapping is TIMESTAMP, see B-4\n"
        + "        DEFAULT_MYSQL_TYPES.put(LocalTime.class, MysqlType.TIME);\n"
        + "        DEFAULT_MYSQL_TYPES.put(Long.class, MysqlType.BIGINT);\n"
        + "        DEFAULT_MYSQL_TYPES.put(OffsetDateTime.class, MysqlType.TIMESTAMP); // default JDBC mapping is TIMESTAMP_WITH_TIMEZONE, see B-4\n"
        + "        DEFAULT_MYSQL_TYPES.put(OffsetTime.class, MysqlType.TIME); // default JDBC mapping is TIME_WITH_TIMEZONE, see B-4\n"
        + "        DEFAULT_MYSQL_TYPES.put(Reader.class, MysqlType.TEXT);\n"
        + "        DEFAULT_MYSQL_TYPES.put(Short.class, MysqlType.SMALLINT);\n"
        + "        DEFAULT_MYSQL_TYPES.put(String.class, MysqlType.VARCHAR);\n"
        + "        DEFAULT_MYSQL_TYPES.put(Time.class, MysqlType.TIME);\n"
        + "        DEFAULT_MYSQL_TYPES.put(Timestamp.class, MysqlType.TIMESTAMP);\n"
        + "        DEFAULT_MYSQL_TYPES.put(ZonedDateTime.class, MysqlType.TIMESTAMP); // no JDBC mapping is defined";

    /**
     * 用来生产测试代码做参考的
     *
     * @throws Exception
     */
    public void gentestcode() throws Exception {
        for (String str : javaCode.split("\n")) {
            //System.out.println(str);
            String[] split = str.split(",");
            String javaType = split[0].split("\\.")[1].replace("put", "").replace('(', ' ').trim();
            String mysqlType = split[1].replace(";", "").replace(')', ' ').trim();
            String javaTypeStr = javaType.substring(0, 1).toUpperCase() + javaType.substring(1);
            String mysqlTypeStr = mysqlType.substring(0, 1).toUpperCase() + mysqlType.substring(1);
            String javaCode = "   public void test_printParameter_" + javaTypeStr + "() throws Exception {\n"
                + "        String sql = \"update t set " + javaTypeStr.toLowerCase() + "_val = ?\"; // " + mysqlTypeStr + "\n"
                + "        String sqlNeed = \"UPDATE t\\nSET " + javaTypeStr.toLowerCase() + "_val = 50000000000\";\n"
                + "        List<Object> parameters = new ArrayList<>();\n"
                + "        parameters.add(new " + javaTypeStr + "());\n"
                + "        String fromatedSql= SQLUtils.format(sql, DbType.mysql, parameters);\n"
                + "        System.out.println(fromatedSql);\n"
                + "        assertEquals(sqlNeed, fromatedSql);\n"
                + "    }\n";
            System.out.println(javaCode);
        }
    }


}
