package j.commonutils.encryde;

import java.util.Base64;

/**
 * @author 刘伯栋
 * @version 1.0
 * @description Use Base64 to encrypt and decrypt content
 * @date 2019/4/22 9:15
 **/
public class Base64Encryption implements EncryptInter {

    public static Base64Encryption getInstance() {
        return new Base64Encryption();
    }

    @Override
    public byte[] encrypt(byte[] input) {
        return Base64.getEncoder().encode(input);
    }

    @Override
    public byte[] decrypt(byte[] input) {
        return Base64.getDecoder().decode(input);
    }
}
