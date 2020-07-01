package com.alibaba.druid.bvt.support.profile;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.support.profile.ProfileEntryKey;
import com.alibaba.druid.support.profile.ProfileEntryReqStat;
import com.alibaba.druid.support.profile.Profiler;

public class ProfilerTest extends TestCase {

    public void test_profile() throws Exception {
        for (int i = 0; i < 10; ++i) {
            req();
        }
    }

    private void req() {
        Profiler.initLocal();

        Profiler.enter("/", Profiler.PROFILE_TYPE_WEB);

        for (int i = 0; i < 100; ++i) {
            execA();
        }

        Assert.assertEquals(2, Profiler.getStatsMap().size());

        {
            ProfileEntryReqStat stat = Profiler.getStatsMap().get(new ProfileEntryKey("/", "com.xxx.a(int)",
                                                                                      Profiler.PROFILE_TYPE_SPRING));
            Assert.assertEquals(100, stat.getExecuteCount());
            Assert.assertEquals(100, stat.getExecuteTimeNanos());
        }

        {
            ProfileEntryReqStat stat = Profiler.getStatsMap().get(new ProfileEntryKey("com.xxx.a(int)",
                                                                                      "com.xxx.b(int)",
                                                                                      Profiler.PROFILE_TYPE_SPRING));
            Assert.assertEquals(1000 * 100, stat.getExecuteCount());
            Assert.assertEquals(1000 * 100, stat.getExecuteTimeNanos());
        }

        Profiler.release(1);

        Assert.assertEquals(3, Profiler.getStatsMap().size());

        Profiler.removeLocal();
    }

    private void execA() {
        Profiler.enter("com.xxx.a(int)", Profiler.PROFILE_TYPE_SPRING);

        for (int i = 0; i < 1000; ++i) {
            execB();
        }

        Profiler.release(1);
    }

    private void execB() {
        Profiler.enter("com.xxx.b(int)", Profiler.PROFILE_TYPE_SPRING);
        Profiler.release(1);
    }
}
