import org.zodiac.commonutils.encryde.EncryptUtil;
import org.zodiac.commonutils.encryde.Encryptions;

import java.io.File;

/**
 * @author 刘伯栋
 * @version 1.0
 * @description TODO
 * @date 2019/4/26 10:27
 **/
public class Test {

    public static void main(String[] args) {
        EncryptUtil.encryptJar(new File("D:\\Code\\Java\\FuturesJava\\out\\artifacts\\FuturesJava_jar\\FuturesJava.jar"),
                new File("D:\\Code\\Java\\FuturesJava\\out\\artifacts\\FuturesJava_jar\\FuturesJava_test.jar"),
                "com.haiyisoft.futures", Encryptions.AES, "");
    }

}
