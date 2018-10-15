package com.alibaba.druid.test;

import java.security.MessageDigest;

public class ThreadLocalCache {

    public static void main(String[] args) throws Exception {
        String str = "\t\n" +
                "If I see the codeof getInstance(), it doesn't seem to create new object, rather it calls Security to get the object Object[] objs = Security.getImpl I wrote test case below: MessageDigest messageDigest1 = MessageDigest.getInstance(\"SHA-1\"); MessageDigest messageDigest2 = MessageDigest.getInstance(\"SHA-1\"); // update and digest and saw that both the messageDigest objects are different, as well their inner objects/buffers are also different. So, I guess ThreadLocal should work. And yes, it is a web server with thread pool. I will use ThreadLocal. Thanks, – Anil Padia Jul 10 '13 at 8:58";
        for (int i = 0; i < 5; ++i) {
            f(str); //
//            f2(str); // 2333
        }
    }


    public static void f(String str) throws Exception {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(str.getBytes());
            messageDigest.digest();
        }
        long millis = System.currentTimeMillis() - start;
        System.out.println("millis : " + millis);
    }

    public static void f2(String str) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            messageDigest.update(str.getBytes());
            messageDigest.digest();
        }
        long millis = System.currentTimeMillis() - start;
        System.out.println("f2 millis : " + millis);
    }
}
