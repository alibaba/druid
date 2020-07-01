package com.alibaba.druid.bvt.pool.xa;

import java.lang.reflect.Constructor;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;
import net.sourceforge.jtds.jdbc.JtdsConnection;

import org.junit.Assert;

import com.alibaba.druid.pool.xa.JtdsXAConnection;
import com.alibaba.druid.pool.xa.JtdsXAResource;

public class JtdsXAConnectionTest extends PoolTestCase {

    public void test_jtds() throws Exception {
        Constructor<JtdsConnection> constrcutor = JtdsConnection.class.getDeclaredConstructor();
        constrcutor.setAccessible(true);
        JtdsConnection jtdsConn = constrcutor.newInstance();
        JtdsXAConnection xaConn = new JtdsXAConnection(jtdsConn);
        Assert.assertSame(jtdsConn, xaConn.getConnection());

        JtdsXAResource xaResource = (JtdsXAResource) xaConn.getXAResource();
        Assert.assertTrue(xaResource.isSameRM(xaResource));
        Assert.assertFalse(xaResource.isSameRM(null));

        {
            Exception error = null;
            try {
                xaResource.commit(null, true);
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
        {
            Exception error = null;
            try {
                xaResource.start(null, 0);
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
        {
            Exception error = null;
            try {
                xaResource.end(null, 0);
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
        {
            Exception error = null;
            try {
                xaResource.forget(null);
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
        {
            Exception error = null;
            try {
                xaResource.rollback(null);
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
        xaResource.recover(0);
        {
            Exception error = null;
            try {
                xaResource.prepare(null);
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
        xaConn.close();
    }
}
