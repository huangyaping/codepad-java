package lang;

import javax.crypto.Cipher;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * Java RSA API Demo.
 */
public class RsaMain {
    private static Cipher decryptCipher;
    private static Cipher encryptCipher;
    static {
        try {
            PublicKey pubKey = readPubKeyFromFile("public.key");
            encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, pubKey);

            PrivateKey priKey = readPriKeyFromFile("private.key");
            decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, priKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] as) throws Exception {
        String s = "hello";
        byte[] cipherData = rsaEncrypt(s.getBytes());
        byte[] data = rsaDecrypt(cipherData);
        assert s.equals(new String(data));
    }

    public static byte[] rsaDecrypt(byte[] data) throws Exception {
        long s = System.currentTimeMillis();
        byte[] cipherData = decryptCipher.doFinal(data);
        long e = System.currentTimeMillis();
        System.out.println("Decrypt cost:"+(e - s));
        return cipherData;
    }

    public static byte[] rsaEncrypt(byte[] data) throws Exception {
        long s = System.currentTimeMillis();
        byte[] cipherData = encryptCipher.doFinal(data);
        long e = System.currentTimeMillis();
        System.out.println("Encrypt cost:"+(e - s));
        return cipherData;
    }

    static PrivateKey readPriKeyFromFile(String keyFileName) throws Exception {
        InputStream in = new FileInputStream("" + keyFileName);
        ObjectInputStream oin =
                new ObjectInputStream(new BufferedInputStream(in));
        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = fact.generatePrivate(keySpec);
            return privateKey;
        } catch (Exception e) {
            throw new RuntimeException("Spurious serialisation error", e);
        } finally {
            oin.close();
        }
    }

    static PublicKey readPubKeyFromFile(String keyFileName) throws Exception {
        InputStream in = new FileInputStream("" + keyFileName);
        ObjectInputStream oin =
                new ObjectInputStream(new BufferedInputStream(in));
        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PublicKey pubKey = fact.generatePublic(keySpec);
            return pubKey;
        } catch (Exception e) {
            throw new RuntimeException("Spurious serialisation error", e);
        } finally {
            oin.close();
        }
    }

    private static void genKey() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair kp = kpg.genKeyPair();
        KeyFactory fact = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(),
                RSAPublicKeySpec.class);
        RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(),
                RSAPrivateKeySpec.class);

        saveToFile("public.key", pub.getModulus(),
                pub.getPublicExponent());
        saveToFile("private.key", priv.getModulus(),
                priv.getPrivateExponent());
    }

    public static void saveToFile(String fileName,
                                  BigInteger mod, BigInteger exp) throws Exception {
        ObjectOutputStream oout = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(fileName)));
        try {
            oout.writeObject(mod);
            oout.writeObject(exp);
        } catch (Exception e) {
            throw new Exception("Unexpected error", e);
        } finally {
            oout.close();
        }
    }
}
