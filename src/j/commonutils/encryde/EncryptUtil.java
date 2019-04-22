package j.commonutils.encryde;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author 刘伯栋
 * @version 1.0
 * @description Util of Encryption and Decryption
 * @date 2019/4/22 10:13
 **/
public class EncryptUtil {

    /**
     * Encrypt the given file
     *
     * @param file        file
     * @param key         key
     * @param encryptions {@link Encryptions}
     * @return bytes
     */
    public static byte[] encryptFile(File file, String key, Encryptions encryptions) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = fileInputStream.readAllBytes();
            switch (encryptions) {
                case AES:
                    return AesEncryption.getInstance().encrypt(bytes, key);
                case BASE64:
                    return Base64Encryption.getInstance().encrypt(bytes);
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Decrypt the given file
     *
     * @param file        file
     * @param key         key
     * @param encryptions {@link Encryptions}
     * @return bytes
     */
    public static byte[] decryptFile(File file, String key, Encryptions encryptions) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = fileInputStream.readAllBytes();
            switch (encryptions) {
                case AES:
                    return AesEncryption.getInstance().decrypt(bytes, key);
                case BASE64:
                    return Base64Encryption.getInstance().decrypt(bytes);
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get the Result File
     * For example: the source file is D:/data/test.txt
     * after encyption using Base64, the Result File is D:/data/test.base64.enc.txt
     *
     * @param file        input file
     * @param encryptions {@link Encryptions}
     * @return
     */
    public static File getEncryptedResultFile(File file, Encryptions encryptions) {
        String fileName = file.getAbsolutePath();
        int separatorCharIndex = fileName.lastIndexOf(".");
        String destFileName = fileName.substring(0, separatorCharIndex);
        String destFileType = fileName.substring(separatorCharIndex);
        return new File(destFileName + "." + encryptions.name().toLowerCase() + ".enc" + destFileType);
    }

    /**
     * Get the Result File
     * For example: the source file is D:/data/test.base64.enc.txt
     * after decyption using Base64, the Result File is D:/data/test.base64.dec.txt
     *
     * @param file
     * @param encryptions
     * @return
     */
    public static File getDecryptedResultFile(File file, Encryptions encryptions) {
        String fileName = file.getAbsolutePath();
        int index = fileName.lastIndexOf("enc");
        if (index > 0) {
            return new File(fileName.substring(0, index) + "dec" + fileName.substring(index + 3));
        } else {
            int separatorCharIndex = fileName.lastIndexOf(".");
            String destFileName = fileName.substring(0, separatorCharIndex);
            String destFileType = fileName.substring(separatorCharIndex);
            return new File(destFileName + "." + encryptions.name().toLowerCase() + ".dec" + destFileType);
        }
    }

}
