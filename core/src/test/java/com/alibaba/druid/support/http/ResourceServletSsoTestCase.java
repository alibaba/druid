package com.alibaba.druid.support.http;

import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Field;

public class ResourceServletSsoTestCase extends TestCase {


    public void testSso() throws Exception {
        final File file = new File("target/test-classes/META-INF/services/com.alibaba.druid.support.http.DruidWebSecurityProvider");
        FileUtils.write(file, DruidWebSecurityProviderMockSsoOk.class.getName());
        ResourceServlet servlet = new ResourceServlet("/test") {
            protected String process(String url) {
                return "mock:" + url;
            }
        };
        final Field securitySpiFld = ResourceServletTestCase.getToNotFinal();
        final DruidWebSecurityProvider spi = (DruidWebSecurityProvider) securitySpiFld.get(null);
        if (spi == null || !(spi instanceof DruidWebSecurityProviderMockSsoOk)) {
            securitySpiFld.set(null, new DruidWebSecurityProviderMockSsoOk());
        }
        final MockServletConfig config = new MockServletConfig();
        config.addInitParameter(ResourceServlet.PARAM_NAME_USERNAME, "user");
        servlet.init(config);
        final MockHttpServletRequest req = new MockHttpServletRequest();

        req.setMethod("GET");
        req.setRequestURI("/test");
        final MockHttpServletResponse res = new MockHttpServletResponse();
        try {
            servlet.service(req, res);
            Assert.assertEquals(200, res.getStatus());
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        file.delete();
    }

    public static final class DruidWebSecurityProviderMockSsoOk implements DruidWebSecurityProvider {
        public boolean isNotPermit(final HttpServletRequest request) {
            return false;
        }
    }
}
