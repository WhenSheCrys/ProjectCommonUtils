import zodiac.java.commonutils.hadoop.HdfsUtil;

public class TestHdfsUtil {
    public static void main(String[] args) {
        HdfsUtil hdfsUtil = new HdfsUtil("hdfs://172.20.32.164:8020", "hdfs");
        String[] txtFiles = hdfsUtil.list("/FKCS_FLINK/processed/201802/LC_CBXX_PART_CBQDBH", x -> x.getPath().toUri().getRawPath().endsWith(".txt"));
        String txtFile = txtFiles[0];
        hdfsUtil.rename(txtFile, hdfsUtil.getParent(txtFile) + "/" + "0947000047456612");
    }
}
