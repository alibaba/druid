package com.alibaba.druid.bvt.filter.config;

import com.alibaba.druid.filter.config.security.decrypter.DecrypterFactoryTest;
import com.alibaba.druid.filter.config.security.decrypter.RsaDecrypterTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Jonas Yang
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        RsaDecrypterTest.class,
        DecrypterFactoryTest.class
})
public class DecrypterTestSuite {
}
