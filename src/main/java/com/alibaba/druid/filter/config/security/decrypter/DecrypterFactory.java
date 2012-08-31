package com.alibaba.druid.filter.config.security.decrypter;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 通过 ID 获取解密器
 * @author Jonas Yang
 */
public class DecrypterFactory {

    private static Log log = LogFactory.getLog(DecrypterFactory.class);

    private static ConcurrentHashMap<String, Decrypter> decrypters = new ConcurrentHashMap<String, Decrypter>();

    static {
        RsaDecrypter rsa = new RsaDecrypter();
        decrypters.put(rsa.getId(), rsa);
    }

    private DecrypterFactory() {}


//    static void initDecrypters(String resource, ClassLoader classLoader) {
//        if (resource == null) {
//            return;
//        }
//
//        InputStream inStream = null;
//
//        try {
//            inStream = classLoader.getResourceAsStream(resource);
//            if (inStream == null) {
//                return;
//            }
//
//            Properties p = new Properties();
//            p.load(inStream);
//
//            for (String key : p.stringPropertyNames()) {
//                String clazzName = p.getProperty(key);
//                Class clazz = classLoader.loadClass(clazzName);
//                Decrypter decrypter = (Decrypter) clazz.newInstance();
//
//                addDecrypter(decrypter);
//            }
//        } catch (Exception e) {
//            log.warn("Fail to init config loaders.", e);
//        } finally {
//            JdbcUtils.close(inStream);
//        }
//    }

    /**
     * 通过 ID 获取解密器, 如果没有找到, 返回 <code>null</code>
     * @param id
     * @return
     */
    public static Decrypter getDecrypter(String id) {
        return  decrypters.get(id);
    }

    public static void addDecrypter(Decrypter decrypter) {
        Decrypter oldDecrypter = decrypters.putIfAbsent(decrypter.getId(), decrypter);
        if (oldDecrypter != null) {
            log.warn("Replace decrypter [" + decrypter.getId() + ", " + oldDecrypter.getClass() + "] with [" + decrypter.getClass() + "]");
        }
    }

    public static Decrypter[] getDecrypters() {
        return decrypters.values().toArray(new Decrypter[0]);
    }
}
