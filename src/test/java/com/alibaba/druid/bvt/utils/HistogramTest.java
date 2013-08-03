package com.alibaba.druid.bvt.utils;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.util.Histogram;

public class HistogramTest extends TestCase {

    public void test_histo() throws Exception {
        Histogram histo = Histogram.makeHistogram(4);
        
        Assert.assertEquals(4, histo.getRanges().length);

        histo.record(0);
        
        histo.record(1);
        histo.record(2);
        
        histo.record(11);
        histo.record(12);
        histo.record(13);
        
        histo.record(101);
        histo.record(102);
        histo.record(103);
        histo.record(104);
        
        histo.record(1001);
        histo.record(1002);
        histo.record(1003);
        histo.record(1004);
        histo.record(1005);
        
        histo.record(10001);
        
        Assert.assertEquals(1, histo.get(0));
        Assert.assertEquals(2, histo.get(1));
        Assert.assertEquals(3, histo.get(2));
        Assert.assertEquals(4, histo.get(3));
        Assert.assertEquals(6, histo.get(4));
        
        histo.toString();
    }
}
