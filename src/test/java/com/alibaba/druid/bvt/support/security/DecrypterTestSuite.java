package com.alibaba.druid.bvt.support.security;

import com.alibaba.druid.support.security.decryptor.AesDecrypterTest;
import com.alibaba.druid.support.security.decryptor.BlowfishDecrypterTest;
import com.alibaba.druid.support.security.decryptor.DesDecrypterTest;
import com.alibaba.druid.support.security.decryptor.RsaDecrypterTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Jonas Yang
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AesDecrypterTest.class,
        DesDecrypterTest.class,
        RsaDecrypterTest.class,
        BlowfishDecrypterTest.class
})
public class DecrypterTestSuite {
}
