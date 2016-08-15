/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.support.http;


import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.support.http.stat.WebAppStat;

public class UserAgentBotStat extends TestCase {

    public void test_youdao() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (compatible; YoudaoBot/1.0; http://www.youdao.com/help/webmaster/spider/; )");

        Assert.assertEquals(1, stat.getBotCount());
        Assert.assertEquals(1, stat.getBotYoudaoCount());
    }

    public void test_bing() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (compatible; bingbot/2.0; +http://www.bing.com/bingbot.htm)");

        Assert.assertEquals(1, stat.getBotCount());
        Assert.assertEquals(1, stat.getBotBingCount());
    }

    public void test_google() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");

        Assert.assertEquals(1, stat.getBotCount());
        Assert.assertEquals(1, stat.getBotGoogleCount());
    }

    public void test_baidu() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)");

        Assert.assertEquals(1, stat.getBotCount());
        Assert.assertEquals(1, stat.getBotBaiduCount());
    }

    public void test_soso() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Sosospider+(+http://help.soso.com/webspider.htm)");

        Assert.assertEquals(1, stat.getBotCount());
        Assert.assertEquals(1, stat.getBotSosoCount());
    }

    public void test_msn() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("msnbot/2.0b (+http://search.msn.com/msnbot.htm)._");

        Assert.assertEquals(1, stat.getBotCount());
        Assert.assertEquals(1, stat.getBotMsnCount());
    }

    public void test_sogou() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Sogou web spider/4.0(+http://www.sogou.com/docs/help/webmasters.htm#07)");

        Assert.assertEquals(1, stat.getBotCount());
        Assert.assertEquals(1, stat.getBotSogouCount());
    }
    
    public void test_yahoo() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (compatible; Yahoo! Slurp; http://help.yahoo.com/help/us/ysearch/slurp)");
        
        Assert.assertEquals(1, stat.getBotCount());
        Assert.assertEquals(1, stat.getBotYahooCount());
    }

    public void test_unkownBot() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 ()");

        Assert.assertEquals(1, stat.getBotCount());
    }

    public void test_unkownBot1() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (compatible; Mail.RU/2.0)");

        Assert.assertEquals(1, stat.getBotCount());
    }

    public void test_unkownBot2() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (compatible; MJ12bot/v1.4.2; http://www.majestic12.co.uk/bot.php?+)");

        Assert.assertEquals(1, stat.getBotCount());
    }

    public void test_unkownBot3() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("-");

        Assert.assertEquals(1, stat.getBotCount());
    }
    
    public void test_unkownBot4() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("\"Mozilla/5.0");
        
        Assert.assertEquals(1, stat.getBotCount());
    }
    
    public void test_unkownBot5() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) Speedy Spider (http://www.entireweb.com/about/search_tech/speedy_spider/)");
        
        Assert.assertEquals(1, stat.getBotCount());
    }
    
    public void test_unkownBot6() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla");
        
        Assert.assertEquals(1, stat.getBotCount());
    }
    
    public void test_unkownBot7() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Crawl/0.01 libcrawl/0.3");
        
        Assert.assertEquals(1, stat.getBotCount());
    }
    
    public void test_unkownBot8() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("SkimBot/1.0 (www.skimlinks.com <dev@skimlinks.com>)");
        
        Assert.assertEquals(1, stat.getBotCount());
    }
    
    public void test_huawei() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("HuaweiSymantecSpider/1.0+DSE-support@huaweisymantec.com+(compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR ; http://www.huaweisymantec.com/en/IRL/spider)");
        
        Assert.assertEquals(1, stat.getBotCount());
    }
    
    public void test_Yeti() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Yeti/1.0 (NHN Corp.; http://help.naver.com/robots/)");
        
        Assert.assertEquals(1, stat.getBotCount());
    }
    
    public void test_KaloogaBot() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (compatible; KaloogaBot; http://kalooga.com/crawler)");
        
        Assert.assertEquals(1, stat.getBotCount());
    }
    
    public void test_YandexBot() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (compatible; YandexBot/3.0; +http://yandex.com/bots)");
        
        Assert.assertEquals(1, stat.getBotCount());
    }
    
    public void test_Ezooms() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (compatible; Ezooms/1.0; ezooms.bot@gmail.com)");
        
        Assert.assertEquals(1, stat.getBotCount());
    }
    
    public void test_Exabot() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (compatible; Exabot/3.0; +http://www.exabot.com/go/robot)");
        
        Assert.assertEquals(1, stat.getBotCount());
    }
    
    public void test_mahonie() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("mahonie, neofonie search:robot/search:robot/0.0.1 (This is the MIA Bot - crawling for mia research project. If you feel unhappy and do not want to be visited by our crawler send an email to spider@neofonie.de; http://spider.neofonie.de; spider@neofonie.de)");
        
        Assert.assertEquals(1, stat.getBotCount());
    }
    
    public void test_AhrefsBot() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (compatible; AhrefsBot/3.0; +http://ahrefs.com/robot/)");
        
        Assert.assertEquals(1, stat.getBotCount());
    }
    
    public void test_Crawler() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (compatible; SISTRIX Crawler; http://crawler.sistrix.net/)");
        
        Assert.assertEquals(1, stat.getBotCount());
    }
    
    public void test_yodao() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (compatible; YodaoBot/1.0; http://www.yodao.com/help/webmaster/spider/; )");
        
        Assert.assertEquals(1, stat.getBotCount());
    }
    
    public void test_BeetleBot() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (compatible; BeetleBot; )");
        
        Assert.assertEquals(1, stat.getBotCount());
    }
    
    public void test_findlinks() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("findlinks/2.1.5 (+http://wortschatz.uni-leipzig.de/findlinks/)");
        
        Assert.assertEquals(1, stat.getBotCount());
    }
    
    public void test_Updownerbot() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Updownerbot (+http://www.updowner.com/bot)");
        
        Assert.assertEquals(1, stat.getBotCount());
    }
    
    public void test_archiveOrgBot() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (compatible; archive.org_bot +http://www.archive.org/details/archive.org_bot)");
        
        Assert.assertEquals(1, stat.getBotCount());
    }
    
    public void test_aiHitBot() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (compatible; aiHitBot/1.1; +http://www.aihit.com/)");
        
        Assert.assertEquals(1, stat.getBotCount());
    }
    
    public void test_DoCoMo() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("DoCoMo/2.0 P900i(c100;TB;W24H11) (compatible; ichiro/mobile goo; +http://search.goo.ne.jp/option/use/sub4/sub4-1/)");
        
        Assert.assertEquals(1, stat.getBotCount());
    }
}
