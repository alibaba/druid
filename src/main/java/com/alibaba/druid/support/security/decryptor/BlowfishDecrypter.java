package com.alibaba.druid.support.security.decryptor;

import com.alibaba.druid.pool.DecryptException;
import com.alibaba.druid.pool.Decrypter;
import com.alibaba.druid.pool.SensitiveParameters;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;

/**
 * 拷贝JBOSS的Blowfish解密
 * @author Jonas Yang
 */
public class BlowfishDecrypter implements Decrypter {

    public SensitiveParameters decrypt(SensitiveParameters parameters) throws DecryptException {
        byte[] kbytes = "jaas is the way".getBytes();
        SecretKeySpec key = new SecretKeySpec(kbytes, "Blowfish");

        BigInteger n = new BigInteger(parameters.getPassword(), 16);
        byte[] encoding = n.toByteArray();

        if (encoding.length % 8 != 0) {
            int length = encoding.length;
            int newLength = ((length / 8) + 1) * 8;
            int pad = newLength - length; //number of leading zeros
            byte[] old = encoding;
            encoding = new byte[newLength];

            for (int i = old.length - 1; i >= 0; i--) {
                   encoding[i + pad] = old[i];
            }
        }

        try {
           Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(encoding);

            return new SensitiveParameters(parameters.getUrl(), parameters.getUsername(), new String(decryptedBytes));
        } catch (Exception e) {
            throw new DecryptException("Failed to decrypt JDBC password.", e);
        }
    }
}
