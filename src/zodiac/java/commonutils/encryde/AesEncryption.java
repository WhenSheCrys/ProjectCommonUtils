package zodiac.java.commonutils.encryde;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author 刘伯栋
 * @version 1.0
 * @description Encrypt and decrypt using Aes
 * @date 2019/4/22 10:16
 **/
public class AesEncryption implements EncryptInter {

    private final int AES_LENGTH = 128;

    /**
     * Static method to get a Instance.
     *
     * @return
     */
    public static final AesEncryption getInstance() {
        return new AesEncryption();
    }

    /**
     * Encrypt the given content
     *
     * @param input input bytes
     * @return encrypted content
     */
    @Override
    public byte[] encrypt(byte[] input) {
        return encrypt(input, "");
    }

    /**
     * Decrypt the given content
     *
     * @param input input bytes
     * @return decrypted content
     */
    @Override
    public byte[] decrypt(byte[] input) {
        return decrypt(input, "");
    }

    /**
     * Encrypt the given content according to the key
     *
     * @param input input bytes
     * @param key   key string
     * @return encrypted content
     */
    public byte[] encrypt(byte[] input, String key) {
        if (null == key) {
            key = "";
        }
        return doEncrypt(input, key.getBytes(), true);
    }

    /**
     * Encrypt the given content according to the key
     *
     * @param input input bytes
     * @param key   key bytes
     * @return encrypted content
     */
    public byte[] encrypt(byte[] input, byte[] key) {
        if (null == key) {
            key = new byte[0];
        }
        return doEncrypt(input, key, true);
    }

    /**
     * Decrypt the given content according to the key
     *
     * @param input input bytes
     * @param key   key bytes
     * @return decrypted content
     */
    public byte[] decrypt(byte[] input, byte[] key) {
        if (null == key) {
            key = new byte[0];
        }
        return doEncrypt(input, key, false);
    }

    /**
     * Final method of doing the encryption and decryption
     *
     * @param input   the input content
     * @param key     the key
     * @param encrypt encrypt or not
     * @return encrypted or decrypted content
     */
    private byte[] doEncrypt(byte[] input, byte[] key, boolean encrypt) {
        Cipher cipher = init(key, encrypt);
        byte[] result = null;
        try {
            result = cipher.doFinal(input);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Decrypt the content according to the key
     *
     * @param input input content
     * @param key   the key
     * @return decrypted content
     */
    public byte[] decrypt(byte[] input, String key) {
        if (null == key) {
            key = "";
        }
        return doEncrypt(input, key.getBytes(), false);
    }

    /**
     * Init the Aes Cipher
     *
     * @param key     key
     * @param encrypt encrypt or not
     * @return
     */
    private Cipher init(byte[] key, boolean encrypt) {
        Cipher cipher = null;
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            if (null == key) {
                key = new byte[0];
            }
            secureRandom.setSeed(key);
            keyGenerator.init(AES_LENGTH, secureRandom);
            SecretKey secretKey = keyGenerator.generateKey();
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            if (encrypt) {
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            }
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return cipher;
    }

}
