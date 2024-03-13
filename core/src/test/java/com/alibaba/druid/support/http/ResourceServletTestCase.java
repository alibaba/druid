package com.alibaba.druid.support.http;

import junit.framework.TestCase;
import org.junit.Assert;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ResourceServletTestCase extends TestCase {

    public void testNotLogin() throws Exception {
        final Field securitySpiFld = getToNotFinal();
        securitySpiFld.set(null, null);
        ResourceServlet servlet = new ResourceServlet("/test") {
            protected String process(String url) {
                return "mock:" + url;
            }
        };
        final MockServletConfig config = new MockServletConfig();
        config.addInitParameter(ResourceServlet.PARAM_NAME_USERNAME, "user");
        servlet.init(config);
        final MockHttpServletRequest req = new MockHttpServletRequest();

        req.setMethod("GET");
        req.setRequestURI("/test");
        final MockHttpServletResponse res = new MockHttpServletResponse();
        try {
            servlet.service(req, res);
            Assert.assertEquals(302, res.getStatus());
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    static Field getToNotFinal() throws NoSuchFieldException, IllegalAccessException {
        final Field securitySpiFld = ResourceServlet.class.getDeclaredField("SECURITY_SPI");
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(securitySpiFld, securitySpiFld.getModifiers() & ~Modifier.FINAL);
        securitySpiFld.setAccessible(true);
        return securitySpiFld;
    }
}
