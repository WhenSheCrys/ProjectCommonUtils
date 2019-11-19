package org.zodiac.commonutils.encryde;

/**
 * @author 刘伯栋
 */
public interface EncryptInter {

    /**
     * encrypt the input bytes
     *
     * @param input input bytes
     * @return encrypted bytes
     */
    byte[] encrypt(byte[] input);

    /**
     * decrypt the input bytes
     *
     * @param input input bytes
     * @return decrypted bytes
     */
    byte[] decrypt(byte[] input);

}
