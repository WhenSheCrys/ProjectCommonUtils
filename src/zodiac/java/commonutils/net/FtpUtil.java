package zodiac.java.commonutils.net;

import org.apache.commons.net.ftp.*;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

/**
 * @author zodiac
 * @version 1.0
 * @date: 2019/10/9 下午2:16
 * @description: todo
 */
public class FtpUtil {

    private static final Logger logger = LogManager.getLogger(FtpUtil.class);

    static {
        logger.setLevel(Level.INFO);
    }

    private String host;
    private String userName;
    private String password;
    private int port = 21;
    private FTPClient ftpClient;
    private String encoding = "UTF-8";

    public FtpUtil(String host, String userName, String password) {
        init(host, userName, password);
        login();
    }

    public FtpUtil(String host, int port, String userName, String password) {
        this.port = port;
        init(host, userName, password);
        login();
    }

    private void init(String host, String userName, String password) {
        this.host = host;
        this.userName = userName;
        this.password = password;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return encoding;
    }

    public FTPClient getFtpClient() {
        if (!ftpClient.isConnected()) {
            login();
        }
        return ftpClient;
    }

    public void disconnect() {
        try {
            ftpClient.logout();
            ftpClient.disconnect();
            ftpClient.quit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void login() {
        this.ftpClient = new FTPClient();
        FTPClientConfig ftpClientConfig = new FTPClientConfig();
        ftpClientConfig.setDefaultDateFormatStr("yyyy-MM-dd HH:mm:ss");
        //ftpClientConfig.setServerLanguageCode("zh");
        ftpClient.configure(ftpClientConfig);
        ftpClient.setControlEncoding("UTF-8");
        try {
            ftpClient.connect(host, port);
            boolean login = ftpClient.login(userName, password);
            if (!login || !FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                logger.error(String.format("Login Failed! UserName: %s, Password: %s", userName, password));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] listFiles(String path) {
        return list(path, FTPFile::isFile);
    }

    public String[] listDirs(String path) {
        return list(path, FTPFile::isDirectory);
    }

    public String[] list(String path) {
        return list(path, null);
    }

    private String[] list(String path, FTPFileFilter ftpFileFilter) {
        String[] files = new String[]{};
        try {
            FTPFile[] ftpFiles;
            if (ftpFileFilter != null) {
                ftpFiles = ftpClient.listFiles(path, ftpFileFilter);
            } else {
                ftpFiles = ftpClient.listFiles(path);
            }
            files = new String[ftpFiles.length];
            for (int i = 0; i < ftpFiles.length; i++) {
                files[i] = Paths.get(path, ftpFiles[i].getName()).toFile().getPath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

    public void downloadFiles(String[] remoteFiles, String localDir) {
        for (String filePath : remoteFiles) {
            String fileName = Paths.get(filePath).toFile().getName();
            String destFile = Paths.get(localDir, fileName).toFile().getPath();
            boolean res = downloadFile(filePath, destFile);
            if (!res) {
                logger.error("Failed to download remote file: " + filePath);
            }
        }
    }

    public boolean downloadFile(String remoteFile, String localFile) {
        boolean res = false;
        try {
            FileOutputStream outputStream = new FileOutputStream(localFile);
            res = ftpClient.retrieveFile(remoteFile, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public boolean uploadFile(String localFile, String remoteFile) {
        boolean res = false;
        try {
            res = ftpClient.storeFile(remoteFile, new FileInputStream(localFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public boolean delete(String path) {
        boolean res = false;
        try {
            if (dirExists(path)) {
                res = true;
                FTPFile[] files = ftpClient.listFiles(path);
                if (files != null && files.length > 0) {
                    for (FTPFile ftpFile : files) {
                        String p = Paths.get(path, ftpFile.getName()).toFile().getPath();
                        res = res && delete(p);
                    }
                }
                res = res && ftpClient.removeDirectory(path);
            } else if (fileExists(path)) {
                res = ftpClient.deleteFile(path);
            } else {
                logger.info(path + " not exists!");
                res = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public void uploadFiles(String[] localFiles, String remoteDir) {
        for (String localFile : localFiles) {
            String localFileName = Paths.get(localFile).toFile().getName();
            String remoteFilePath = Paths.get(remoteDir, localFileName).toFile().getPath();
            boolean res = uploadFile(localFile, remoteFilePath);
            if (!res) {
                logger.error("Failed to upload file to " + remoteFilePath);
            }
        }
    }

    public boolean mkdir(String dir) {
        boolean res = false;
        try {
            if (dirExists(dir)) {
                logger.info(dir + " already exists!");
                res = true;
            } else {
                res = ftpClient.makeDirectory(dir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public boolean fileExists(String remoteFile) {
        boolean exists = false;
        try {
            InputStream inputStream = ftpClient.retrieveFileStream(remoteFile);
            exists = (inputStream != null && ftpClient.getReplyCode() != 550);
            if (inputStream != null) {
                inputStream.close();
                ftpClient.completePendingCommand();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists;
    }

    public boolean dirExists(String remoteDir) {
        boolean exists = false;
        try {
            String currentWorkingDir = ftpClient.printWorkingDirectory();
            exists = ftpClient.changeWorkingDirectory(remoteDir);
            ftpClient.changeWorkingDirectory(currentWorkingDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists;
    }
}
