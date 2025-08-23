package com.alibaba.druid.bvt.pool.xa;

import static org.junit.Assert.*;


import java.lang.reflect.Constructor;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;
import net.sourceforge.jtds.jdbc.JtdsConnection;


import com.alibaba.druid.pool.xa.JtdsXAConnection;
import com.alibaba.druid.pool.xa.JtdsXAResource;

public class JtdsXAConnectionTest extends PoolTestCase {
    public void test_jtds() throws Exception {
        Constructor<JtdsConnection> constrcutor = JtdsConnection.class.getDeclaredConstructor();
        constrcutor.setAccessible(true);
        JtdsConnection jtdsConn = constrcutor.newInstance();
        JtdsXAConnection xaConn = new JtdsXAConnection(jtdsConn);
        assertSame(jtdsConn, xaConn.getConnection());

        JtdsXAResource xaResource = (JtdsXAResource) xaConn.getXAResource();
        assertTrue(xaResource.isSameRM(xaResource));
        assertFalse(xaResource.isSameRM(null));

        {
            Exception error = null;
            try {
                xaResource.commit(null, true);
            } catch (Exception ex) {
                error = ex;
            }
            assertNotNull(error);
        }
        {
            Exception error = null;
            try {
                xaResource.start(null, 0);
            } catch (Exception ex) {
                error = ex;
            }
            assertNotNull(error);
        }
        {
            Exception error = null;
            try {
                xaResource.end(null, 0);
            } catch (Exception ex) {
                error = ex;
            }
            assertNotNull(error);
        }
        {
            Exception error = null;
            try {
                xaResource.forget(null);
            } catch (Exception ex) {
                error = ex;
            }
            assertNotNull(error);
        }
        {
            Exception error = null;
            try {
                xaResource.rollback(null);
            } catch (Exception ex) {
                error = ex;
            }
            assertNotNull(error);
        }
        xaResource.recover(0);
        {
            Exception error = null;
            try {
                xaResource.prepare(null);
            } catch (Exception ex) {
                error = ex;
            }
            assertNotNull(error);
        }
        xaConn.close();
    }
}
