package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DecryptException;
import com.alibaba.druid.pool.Decrypter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.SensitiveParameters;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;

/**
 * @author Jonas Yang
 */
public class DruidDataSourceDecryptTest {

    @Test
    public void testDecrypt() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:oracle:thin:@");
        dataSource.setUsername("xiaoyu");
        dataSource.setPassword("druid");

        dataSource.setDecrypter(new Decrypter() {
            @Override
            public SensitiveParameters decrypt(SensitiveParameters parameters) throws DecryptException {
                Assert.assertEquals("xiaoyu", parameters.getUsername());
                throw new DecryptException("Test");
            }
        });

        try {
            dataSource.init();
            dataSource.createPhysicalConnection();
            Assert.assertTrue("Got Here.", false);
        } catch (SQLException e) {
            Assert.assertEquals("The cause clas is " + e.getCause().getClass(), DecryptException.class, e.getCause().getClass());
            Assert.assertEquals("The excepiton message is " + e.getCause().getMessage(), "Test", e.getCause().getMessage());
        } finally {
            JdbcUtils.close(dataSource);
        }
    }
}
