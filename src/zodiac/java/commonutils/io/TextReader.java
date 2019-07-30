package zodiac.java.commonutils.io;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Scanner;


public class TextReader {

    /**
     * 读取前20行
     *
     * @param file    文件
     * @param charset 编码
     * @return {@link java.util.ArrayList<String>}
     */
    public static ArrayList<String> top(File file, Charset charset) {
        return top(file, charset, 20);
    }

    /**
     * 读取前N行
     *
     * @param file    文件
     * @param charset 编码
     * @param n       行数
     * @return {@link java.util.ArrayList<String>}
     */
    public static ArrayList<String> top(File file, Charset charset, int n) {
        ArrayList<String> arrayList = new ArrayList<>();
        if (n < 1) {
            return arrayList;
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
            //采用scanner减少内存消耗
            Scanner scanner = new Scanner(reader);
            int lineNum = 0;
            while (lineNum < n && scanner.hasNextLine()) {
                arrayList.add(scanner.nextLine());
                lineNum++;
            }
            return arrayList;
        } catch (IOException e) {
            e.printStackTrace();
            return arrayList;
        }
    }

    /**
     * 读取最后几行
     *
     * @param file     文件
     * @param charset  编码
     * @param n        条数
     * @param positive 是否正序
     * @return {@link java.util.ArrayList<String>}
     */
    public static ArrayList<String> tail(File file, int n, Charset charset, boolean positive) {
        ArrayList<String> arrayList = new ArrayList<>();
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
            //获取文件长度
            long length = randomAccessFile.length();
            //如果长度小于1，则说明文件为空
            if (length < 1) {
                return arrayList;
            }
            long available = length - 1;
            //标记文件最后位置
            randomAccessFile.seek(available);
            for (int i = 0; i < n && available > 0; i++) {
                if (available == length - 1) {
                    available--;
                    randomAccessFile.seek(available);
                }
                int c = randomAccessFile.read();
                while (c != '\n' && available > 0) {
                    available--;
                    randomAccessFile.seek(available);
                    c = randomAccessFile.read();
                }
                //标记当前位置
                long nowPointer = randomAccessFile.getFilePointer();
                String line = new String(randomAccessFile.readLine().getBytes(), charset);
                arrayList.add(line);
                if (nowPointer > 1) {
                    randomAccessFile.seek(nowPointer - 2);
                }
            }
            if (!positive) {
                Collections.reverse(arrayList);
            }
            return arrayList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert Objects.nonNull(randomAccessFile);
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }
}
