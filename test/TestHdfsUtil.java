import org.zodiac.commonutils.hadoop.HdfsUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class TestHdfsUtil {
    public static void main(String[] args) {
        HdfsUtil hdfsUtil = new HdfsUtil("hdfs://172.20.32.164:8020", "hdfs");
        String[] txtFiles = hdfsUtil.list("/FKCS_FLINK/result/flink");
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@172.20.36.25:1521:hydb", "PMS_SZ", "PMS_SZ");
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO FLINK_JLDXX (GZDBH, JLDBH, YWLBDM, ZXYGZZDL, ZXYGFZDL, ZXYGPZDL, ZXYGGZDL, ZDF) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            for (String txtFile : txtFiles) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(hdfsUtil.getInputStream(txtFile)));
                reader.lines().parallel().forEach(line -> {
                    String[] arr = line.split(",", -1);
                    String[] zdlStr = arr[3].split("\\|", -1);
                    BigDecimal[] zld = new BigDecimal[zdlStr.length];
                    for (int i = 0; i < zdlStr.length; i++) {
                        if (null != zdlStr[i] || !zdlStr[i].equals("") || !zdlStr[i].equals("null")) {
                            zld[i] = new BigDecimal(Objects.requireNonNull(zdlStr[i]));
                        }
                    }
                    if (zdlStr.length >= 4) {
                        try {
                            preparedStatement.setString(1, arr[0]);
                            preparedStatement.setString(2, arr[1]);
                            preparedStatement.setString(3, arr[2]);
                            preparedStatement.setBigDecimal(4, zld[0]);
                            preparedStatement.setBigDecimal(5, zld[1]);
                            preparedStatement.setBigDecimal(6, zld[2]);
                            preparedStatement.setBigDecimal(7, zld[3]);
                            preparedStatement.setBigDecimal(8, new BigDecimal(arr[4]));
                            preparedStatement.addBatch();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
                long[] result = preparedStatement.executeLargeBatch();
                preparedStatement.clearBatch();
                connection.commit();
                System.out.println("Insert " + result.length);
            }
            preparedStatement.close();
            connection.commit();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
