package com.alibaba.druid.bvt.pool.basic;

import java.util.Hashtable;

import javax.naming.RefAddr;
import javax.naming.Reference;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSourceFactory;


public class DruidDataSourceFactoryTest extends TestCase {
    @SuppressWarnings("serial")
    public void test_factory() throws Exception {
        DruidDataSourceFactory factory = new DruidDataSourceFactory();
        
        Assert.assertNull(factory.getObjectInstance(null, null, null, null));
        Assert.assertNull(factory.getObjectInstance(new Reference("javax.sql.Date"), null, null, null));
        
        Reference ref = new Reference("javax.sql.DataSource");
        ref.add(new RefAddr("user") {
            @Override
            public Object getContent() {
                return null;
            }
            
        });
        ref.add(new RefAddr("defaultReadOnly") {
            @Override
            public Object getContent() {
                return Boolean.TRUE;
            }
            
        });
        
        factory.getObjectInstance(ref, null, null, new Hashtable<Object, Object>());
    }
    
}
