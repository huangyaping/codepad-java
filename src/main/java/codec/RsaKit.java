package codec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * RSA code.
 * @author huangyaping
 * @date 2017/10/10
 */
public class RsaKit {

    /**
     * encrypt algorithm
     */
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * public/private key size
     */
    private static final Integer KEY_SIZE = 1024;

    /**
     * RSA最大解密密文大小，随着 KEY_SIZE 而变化。
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * RSA最大加密明文大小，随着 KEY_SIZE 而变化。
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    public static void main(String[] args) throws Exception {
        String src = "ABCDEFGHIJKLMNOPURSTUVWXYZabcdefghijklmnopurstuvwxyz1234567890/*-+.`~|;:,%=!@#$^&_";

        String[] keyPairStr = genKeyPairStr();
        System.out.println("PublicKeyStr="+keyPairStr[0]);
        System.out.println("PrivateKeyStr="+keyPairStr[1]);
        PublicKey publicKey = loadPublicKey(keyPairStr[0]);
        PrivateKey privateKey = loadPrivateKey(keyPairStr[1]);

        String message = "{\"firstName\":\"John\",\"lastName\":\"Smith\",\"isAlive\":true,\"age\":25,\"address\":{\"streetAddress\":\"212ndStreet\",\"city\":\"NewYork\",\"state\":\"NY\",\"postalCode\":\"10021-3100\"},\"phoneNumbers\":[{\"type\":\"home\",\"number\":\"212555-1234\"},{\"type\":\"office\",\"number\":\"646555-4567\"},{\"type\":\"mobile\",\"number\":\"123456-7890\"}],\"children\":[],\"spouse\":null}";
        String encrypted = encryptByPublicKey(message, publicKey);
        System.out.println(encrypted);
        String msg = decryptByPrivateKey(encrypted, privateKey);
        System.out.println(msg);

    }

    public static String encryptByPublicKey(String message, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return doEncrypt(message, cipher);
    }

    private static String doEncrypt(String message, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] messageBytes = message.getBytes();
        int inputLen = messageBytes.length;
        for(int offSet = 0; offSet < inputLen; offSet += MAX_ENCRYPT_BLOCK) {
            int len = MAX_ENCRYPT_BLOCK;
            if(offSet + MAX_ENCRYPT_BLOCK > inputLen) {
                len = inputLen - offSet;
            }
            byte[] parts = cipher.doFinal(messageBytes, offSet, len);
            out.write(parts);
        }
        return Base64.encodeBase64String(out.toByteArray());
    }

    public static String decryptByPrivateKey(String encrypted, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return doDecrypt(encrypted, cipher);
    }

    private static String doDecrypt(String encrypted, Cipher cipher) throws Exception {
        byte[] encryptedBytes = Base64.decodeBase64(encrypted);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int inputLen = encryptedBytes.length;
        for(int offSet = 0; offSet < inputLen; offSet += MAX_DECRYPT_BLOCK) {
            byte[] parts = cipher.doFinal(encryptedBytes, offSet, MAX_DECRYPT_BLOCK);
            out.write(parts);
        }
        return new String(out.toByteArray());
    }

    public static PublicKey loadPublicKey(String publicKeyStr) throws Exception {
        byte[] buffer = Base64.decodeBase64(publicKeyStr);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
        return keyFactory.generatePublic(keySpec);
    }

    public static PrivateKey loadPrivateKey(String privateKeyStr) throws Exception {
        byte[] buffer = Base64.decodeBase64(privateKeyStr);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 生成公钥、私钥对的字符串形式。
     * key保存在Base64字符串，公钥使用 X509EncodedKeySpec 编码 私钥使用 PKCS8EncodedKeySpec 编码。
     * @return
     * @throws Exception
     */
    private static String[] genKeyPairStr() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        return new String[]{Base64.encodeBase64String(publicKey.getEncoded()), Base64.encodeBase64String(privateKey.getEncoded())};
    }

}
