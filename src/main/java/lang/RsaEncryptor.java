package lang;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA加密
 */
public class RsaEncryptor {
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;
    /**
     * 公钥字符串
     */
    private static final String PUBLIC_KEY_STR = "";
    private static Cipher ecipher;

    static {
        try {
            ecipher = Cipher.getInstance("RSA");
            PublicKey publicKey = loadPublicKey(PUBLIC_KEY_STR);
            ecipher.init(Cipher.ENCRYPT_MODE, publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] as) {
        // 明文字符串
        String src = "ABCDEFGHIJKLMNOPURSTUVWXYZabcdefghijklmnopurstuvwxyz1234567890/*-+.`~|;:,%=!@#$^&_";
        String encryptedStr = encrypt(src);
        System.out.println(encryptedStr);
    }

    /**
     * 加密明文。
     *
     * @param str 明文
     * @return
     */
    public static String encrypt(String str) {
        try {
            return Base64Utils.encode(doEncrypt(str.getBytes()));
        } catch (Exception e) {
            return null;
        }
    }

    private static byte[] doEncrypt(byte[] data) throws Exception {
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = ecipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = ecipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            offSet += MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr 公钥数据字符串
     * @throws Exception 加载公钥时产生的异常
     */
    public static PublicKey loadPublicKey(String publicKeyStr) throws Exception {
        try {
            byte[] buffer = Base64Utils.decode(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }
    }


}
