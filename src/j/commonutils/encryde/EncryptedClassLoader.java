package j.commonutils.encryde;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * @author 刘伯栋
 * @version 1.0
 * @description Load classes from jar that is Encrypted
 * <code>
 * EncryptedClassLoader encryptedClassLoader = new EncryptedClassLoader("com.test.main", Encryptions.AES);
 * Thread.currentThread().setContextClassLoader(encryptedClassLoader);
 * Class c = encryptedClassLoader.loadClass("com.test.main.Main", false);
 * Method mainMethod = c.getMethod("main", String[].class);
 * mainMethod.invoke(null, (Object) new String[]{});
 * </code>
 * @date 2019/4/22 14:55
 **/
public class EncryptedClassLoader extends ClassLoader {
    /**
     * Calss prefix that is encrypted
     */
    private String prefix;
    /**
     * {@link Encryptions}
     */
    private Encryptions encryptions = Encryptions.AES;
    /**
     * Private key
     */
    private byte[] key;

    public EncryptedClassLoader() {
        super();
        this.prefix = "";
    }

    public EncryptedClassLoader(String prefix, Encryptions encryptions) {
        super();
        if (null == prefix || null == this.prefix) {
            this.prefix = "";
        }
        this.prefix = prefix;
        this.encryptions = encryptions;
    }

    public void setKey(String key) {
        if (null == key) {
            key = "";
        }
        this.key = key.getBytes();
    }

    public void setKey(byte[] key) {
        if (null == key) {
            key = new byte[0];
        }
        this.key = key;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class c = null;
        //search from already loaded classes
        c = findLoadedClass(name);
        if (null != c) {
            return c;
        }
        //try to load custom class
        if (name.startsWith(prefix) && !name.endsWith(this.getClass().getSimpleName())) {
            try {
                String path = name.replaceAll("\\.", "/") + ".class";
                InputStream inputStream = getResourceAsStream(path);
                assert inputStream != null;
                int length = inputStream.available();
                byte[] fileData = new byte[length];
                for (int i = 0; i < length; i++) {
                    int r = inputStream.read(fileData, i, 1);
                    if (r == -1) {
                        break;
                    }
                }
                inputStream.close();
                byte[] classData;
                switch (encryptions) {
                    case BASE64:
                        classData = Base64Encryption.getInstance().decrypt(fileData);
                        break;
                    case AES:
                        if (null == key || Arrays.equals(key, new byte[0])) {
                            classData = AesEncryption.getInstance().decrypt(fileData);
                        } else {
                            classData = AesEncryption.getInstance().decrypt(fileData, key);
                        }
                        break;
                    default:
                        classData = fileData;
                }
                c = defineClass(name, classData, 0, classData.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //search from System classes
            c = findSystemClass(name);
        }
        if (null == c) {
            //if class still not found, use parent's method
            c = super.loadClass(name, resolve);
        }
        if (resolve && null != c) {
            resolveClass(c);
        }
        return c;
    }
}
