package com.alibaba.druid.bvt.utils;

import junit.framework.TestCase;

import org.junit.Assert;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.alibaba.druid.util.DruidWebUtils;

public class DruidWebUtilsTest extends TestCase {
    public void test_getContextPath_2_5() throws Exception {
        new DruidWebUtils(); //

        MockServletContext context = new MockServletContext() {
            public int getMajorVersion() {
                return 2;
            }

            public int getMinorVersion() {
                return 4;
            }

            public String getContextPath() {
                throw new NoSuchMethodError();
            }
        };

        Assert.assertNull(DruidWebUtils.getContextPath(context));
    }

    /**
     * @throws Exception
     * @see <a href="https://github.com/alibaba/druid/issues/5180">...</a>
     * see <a href="https://github.com/alibaba/druid/issues/5072">...</a>
     */
    public void test_getxforwardip() throws Exception {
        MockHttpServletRequest request1 = new MockHttpServletRequest() {
            public String getHeader(String name) {
                if ("X-Forwarded-For".equalsIgnoreCase(name)) {
                    return "116.228.20.212 , 10.0.25.22";
                }
                return super.getHeader(name);
            }
        };
        String ip1 = DruidWebUtils.getRemoteAddr(request1);
        System.out.println("X-Forwarded-For ip1===" + ip1);
        assertEquals("116.228.20.212", ip1);
        MockHttpServletRequest request2 = new MockHttpServletRequest() {
            public String getHeader(String name) {
                if ("X-Forwarded-For".equalsIgnoreCase(name)) {
                    return "10.0.25.22";
                }
                return super.getHeader(name);
            }
        };
        String ip2 = DruidWebUtils.getRemoteAddr(request2);
        System.out.println("X-Forwarded-For ip2===" + ip2);
        assertEquals("10.0.25.22", ip2);

        MockHttpServletRequest request3 = new MockHttpServletRequest() {
            public String getHeader(String name) {
                if ("X-Forwarded-For".equalsIgnoreCase(name)) {
                    return "x10.0.25.22";
                }
                return "192.168.1.3";
            }
        };
        String ip3 = DruidWebUtils.getRemoteAddr(request3);
        System.out.println("X-Forwarded-For ip3===" + ip3);
        assertEquals("192.168.1.3", ip3);
    }
}
