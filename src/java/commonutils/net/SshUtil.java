package java.commonutils.net;

import com.jcraft.jsch.*;
import java.commonutils.text.StringUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class SshUtil {

    private static SshUtil newInstance;
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final com.jcraft.jsch.Logger jschLogger = new JschLogger();

    private SshUtil() {

    }

    public static SshUtil newInstance() {
        if (null == newInstance) {
            newInstance = new SshUtil();
        }
        return newInstance;
    }

    public Builder builder() {
        return new Builder();
    }

    public static class AuthType {
        public static final int AUTH_PASSWORD = 1;
        public static final int AUTH_PRIVATE_KEY = 2;
    }

    public class Builder {

        private int authType = AuthType.AUTH_PASSWORD;

        private String host;

        private int port = 22;

        private String user;

        private String password;

        private int timeout = 3000;

        private String prvKeyFile;

        private String pubKeyFile;

        private String passphrase = null;


        Builder() {

        }

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setUser(String user) {
            this.user = user;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder setPrvKeyFile(String prvKeyFile) {
            this.prvKeyFile = prvKeyFile;
            return this;
        }

        public Builder setPassphrase(String passphrase) {
            this.passphrase = passphrase;
            return this;
        }

        public Builder setPubKeyFile(String pubKeyFile) {
            this.pubKeyFile = pubKeyFile;
            return this;
        }

        public Builder setAuthType(int authType) {
            this.authType = authType;
            return this;
        }

        public SshConnector finish() {
            JSch.setLogger(jschLogger);
            if (StringUtil.isEmpty(host)) {
                throw new IllegalArgumentException("Host must not be Empty!");
            }
            if (port <= 0) {
                throw new IllegalArgumentException("Port must not be Negative or Zero!");
            }
            if (StringUtil.isEmpty(user)) {
                throw new IllegalArgumentException("User must not be Empty!");
            }
            if (timeout <= 0) {
                throw new IllegalArgumentException("Timeout must not be Negative or Zero!");
            }
            SshConnector connector = null;
            if (authType == AuthType.AUTH_PRIVATE_KEY) {
                if (StringUtil.isEmpty(prvKeyFile) || prvKeyFile.matches("\\s+")) {
                    throw new IllegalArgumentException("PrvKeyFile must not be Empty!");
                }
                connector = new SshConnector(host, port, user, prvKeyFile, pubKeyFile, passphrase, 3000);
            } else {
                if (StringUtil.isEmpty(password)) {
                    throw new IllegalArgumentException("Password must not be Empty!");
                }
                connector = new SshConnector(host, port, user, password, timeout);
            }
            return connector;
        }
    }

    public class SshConnector {

        //缓冲大小
        private final int bufferSize = 1024 * 1024;
        private final JSch jSch = new JSch();
        private String host;
        private int port;
        private String user;
        private String password;
        private int timeout;
        private Session session = null;

        private ChannelSftp channelSftp;

        private int authTpye;

        private String prvKeyFile;

        private String pubKeyFile;

        private UserInfo ui;

        private String passphrase = "";

        private SshConnector(String host, int port, String user, String password, int timeout) {
            this.host = host;
            this.port = port;
            this.user = user;
            this.password = password;
            this.timeout = timeout;
            this.authTpye = AuthType.AUTH_PASSWORD;
        }

        private SshConnector(String host, int port, String user, String prvKeyFile, String pubKeyFile, String passphrase, int timeout) {
            this.host = host;
            this.port = port;
            this.user = user;
            this.timeout = timeout;
            this.prvKeyFile = prvKeyFile;
            this.pubKeyFile = pubKeyFile;
            this.passphrase = passphrase;
            this.authTpye = AuthType.AUTH_PRIVATE_KEY;

            ui = new UserInfo() {
                @Override
                public String getPassphrase() {
                    return passphrase;
                }

                @Override
                public String getPassword() {
                    return null;
                }

                @Override
                public boolean promptPassword(String message) {
                    return false;
                }

                @Override
                public boolean promptPassphrase(String message) {
                    return false;
                }

                @Override
                public boolean promptYesNo(String message) {
                    return false;
                }

                @Override
                public void showMessage(String message) {
                    logger.info(message);
                    System.out.println(message);
                }
            };
        }

        /**
         * 获取session
         *
         * @return session
         */
        private Session getSession() {
            if (null == session || !session.isConnected()) {
                if (authTpye == AuthType.AUTH_PRIVATE_KEY) {
                    try {
                        if (null == pubKeyFile || StringUtil.isEmpty(pubKeyFile) || pubKeyFile.matches("\\s+")) {
                            if (null == passphrase) {
                                jSch.addIdentity(prvKeyFile);
                            } else {
                                jSch.addIdentity(prvKeyFile, passphrase);
                            }
                        } else {
                            if (null == passphrase) {
                                jSch.addIdentity(prvKeyFile, pubKeyFile, "".getBytes());
                            } else {
                                jSch.addIdentity(prvKeyFile, pubKeyFile, passphrase.getBytes());
                            }
                        }
                        session = jSch.getSession(user, host, port);
                        session.setConfig("StrictHostKeyChecking", "no");
                        session.setUserInfo(ui);
                        session.connect(timeout);
                    } catch (JSchException e) {
                        logger.error(String.format("Error when get Session on %s %s", host, e.getCause() + "\n" + e.getMessage()), e);
                    }
                    if (null != session && session.isConnected()) {
                        logger.info(String.format("Connected to %s use key_file %s !", host, prvKeyFile));
                    } else {
                        logger.error(String.format("Connet to %s use key_file %s Failed !", host, prvKeyFile));
                    }
                } else {
                    try {
                        session = jSch.getSession(user, host, port);
                    } catch (JSchException e) {
                        logger.error(String.format("Error when get Session on %s %s", host, e.getCause() + "\n" + e.getMessage()), e);
                        e.printStackTrace();
                    }
                    assert session != null;
                    session.setPassword(password);
                    session.setConfig("StrictHostKeyChecking", "no");
                    try {
                        session.connect(timeout);
                    } catch (JSchException e) {
                        logger.error(String.format("Error when Connect to host: %s with port: %d, user: %s", host, port, user));
                        e.printStackTrace();
                    }
                }
                if (null != session && session.isConnected()) {
                    logger.info(String.format("Conneted to %s !", host));
                } else {
                    logger.error(String.format("Connet to %s Failed !", host));
                }
                return session;
            } else {
                return session;
            }
        }

        /**
         * 执行命令
         *
         * @param cmd 命令
         */
        public void execCmd(String cmd) {
            int maxOutput = 10240;
            execCmd(cmd, maxOutput);
        }

        /**
         * 执行命令
         *
         * @param cmd        命令
         * @param backGround 是否后台运行
         */
        public void execCmd(String cmd, boolean backGround) {
            int maxOutput = 10240;
            execCmd(cmd, backGround, maxOutput);
        }

        /**
         * 执行命令
         *
         * @param cmd       命令
         * @param maxOutput 最大输出字符数
         */
        public void execCmd(String cmd, int maxOutput) {
            execCmd(cmd, false, maxOutput);
        }

        /**
         * 执行命令
         *
         * @param cmd        命令
         * @param backGround 是否后台运行
         * @param maxOutput  最大输出字符数
         */
        public void execCmd(String cmd, boolean backGround, int maxOutput) {
            session = getSession();
            assert !StringUtil.isEmpty(cmd);

            if (backGround) {
                StringBuilder builder = new StringBuilder();
                if (!cmd.startsWith("nohup ")) {
                    builder.append("nohup ");
                }
                builder.append(cmd);
                if (!cmd.endsWith(" &")) {
                    builder.append(" &");
                }
                cmd = builder.toString();
            }

            ChannelExec channel = null;
            try {
                channel = (ChannelExec) session.openChannel("exec");
            } catch (JSchException e) {
                logger.error(String.format("Error when get Chanel on host: %s with port: %d, user: %s. %s", host, port, user, e.getCause() + "\n" + e.getMessage()));
                e.printStackTrace();
            }
            assert null != channel && !channel.isClosed();
            channel.setCommand(cmd);
            try {
                logger.info(String.format("Execute commond: %s ...", cmd));
                channel.connect();
            } catch (JSchException e) {
                logger.error(String.format("Error when Connect to Channel on host: %s with port: %d, user: %s. %s", host, port, user, e.getCause() + "\n" + e.getMessage()));
                closeChannel(channel);
                e.printStackTrace();
            }

            InputStream in = null;
            try {
                in = channel.getInputStream();
            } catch (IOException e) {
                logger.error(String.format("Error when Get OutputStream of command: %s on host: %s with port: %d, user: %s. %s", cmd, host, port, user, e.getCause() + "\n" + e.getMessage()));
                closeChannel(channel);
                e.printStackTrace();
            }
            assert null != in;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
            int l = 0;
            String line;
            try {
                while ((line = reader.readLine()) != null && l <= maxOutput) {
                    l += line.length();
                    System.out.println(line);
                }
            } catch (IOException e) {
                logger.error(String.format("Error when Read from commamd: %s Output! %s", cmd, e.getCause() + "\n" + e.getMessage()), e);
                e.printStackTrace();
            } finally {
                closeChannel(channel);
            }
        }


        /**
         * 上传文件
         *
         * @param sourceFile 源文件
         * @param destFile   目标文件
         * @param overwrite  是否覆盖
         */
        public boolean upload(String sourceFile, String destFile, boolean overwrite) {
            boolean ret = false;
            try {
                upload(new File(sourceFile), destFile, overwrite);
                ret = true;
            } catch (IOException e) {
                logger.error(String.format("Error when upload %s to %s %s", sourceFile, destFile, e.getCause() + "\n" + e.getMessage()), e);
            }
            return ret;
        }

        /**
         * 获取远程的真实地址
         *
         * @param path
         * @return
         */
        public String realPath(String path) {
            channelSftp = getChannelSftp();
            String ret = null;
            try {
                ret = channelSftp.realpath(path);
            } catch (SftpException e) {
                logger.error("Error when get RealPath");
                e.printStackTrace();
            }
            return ret;
        }

        /**
         * 上传
         *
         * @param sourceFile 源文件
         * @param destFile   目标文件
         * @param overwrite  是否覆盖
         * @throws IOException
         */
        public void upload(File sourceFile, String destFile, boolean overwrite) throws IOException {
            channelSftp = getChannelSftp();
            long total = sourceFile.length();
            String realDestPath = realPath(destFile);
            if (sourceFile.isDirectory()) {
                File[] filelist = sourceFile.listFiles();
                if (filelist.length > 0) {
                    for (File file : filelist) {
                        String dest = realDestPath + "/" + file.getName();
                        if (!exists(dest)) {
                            mkdir(getParentDir(dest));
                        }
                        upload(file, dest, overwrite);
                    }
                }
            } else {
                if (overwrite || !exists(destFile)) {
                    BufferedInputStream reader = null;
                    try {
                        reader = new BufferedInputStream(new FileInputStream(sourceFile));
                    } catch (FileNotFoundException e) {
                        logger.error(String.format("File %s not found ", sourceFile));
                        e.printStackTrace();
                    }
                    if (null == reader) {
                        throw new RuntimeException("File reader cant not be null!");
                    }
                    if (total < bufferSize) {
                        byte[] buf = new byte[(int) total];
                        if (reader.read(buf) != -1) {
                            upload(buf, destFile, ChannelSftp.OVERWRITE);
                        }
                    } else {
                        byte[] buf = new byte[bufferSize];
                        if (reader.read(buf) != -1) {
                            upload(buf, destFile, ChannelSftp.OVERWRITE);
                        }
                        long writed = bufferSize;
                        while ((total - writed) >= bufferSize) {
                            if (reader.read(buf) != -1) {
                                upload(buf, destFile, ChannelSftp.APPEND);
                                writed += bufferSize;
                            }
                        }
                        if ((total - writed) > 0) {
                            buf = new byte[(int) (total - writed)];
                            if (reader.read(buf) != -1) {
                                upload(buf, destFile, ChannelSftp.APPEND);
                            }
                        }
                    }
                    reader.close();
                } else {
                    logger.warn(String.format("Not Overwirte ! File %s already exist!", destFile));
                }
                checkFile(sourceFile.getAbsolutePath(), destFile);
            }
        }


        private void checkFile(String source, String dest) {
            File sourceFile = new File(source);
            SftpATTRS destAttrs = getAttr(dest);
            if (null == destAttrs) {
                logger.error(String.format("Can't get attrs of %s", dest));
            } else {
                boolean isTheSameLength = sourceFile.length() == destAttrs.getSize();
                if (!isTheSameLength) {
                    logger.warn(String.format("Check Failed, will delete %s", dest));
                    delete(dest);
                } else {
                    logger.info("Check Success!");
                }
            }
        }

        public void uploadContent(String content, String dest) {
            upload(content.getBytes(), dest, ChannelSftp.OVERWRITE);
        }

        private void upload(String source, String dest) {
            channelSftp = getChannelSftp();
            File sourceFile = new File(source);
            String realPath = realPath(dest);
            if (sourceFile.isDirectory()) {
                File[] files = sourceFile.listFiles();
                if (files.length > 0) {
                    for (File file : files) {
                        String destFile = realPath + "/" + file.getName();
                        mkdir(getParentDir(destFile));
                        upload(file.getAbsolutePath(), destFile);
                    }
                }
            } else {
                try {
                    SftpProgressMonitor sftpProgressMonitor = new MySftpProgressMonitor();
                    channelSftp.put(source, dest, sftpProgressMonitor);
                } catch (SftpException e) {
                    logger.error(String.format("Error when upload %s to %s %s", source, dest, e.getCause() + "\n" + e.getMessage()));
                    e.printStackTrace();
                }
            }
            checkFile(source, dest);
        }

        /**
         * 上传数据
         *
         * @param content  内容 byte数组
         * @param destFile 目标文件
         * @param mode     模式
         */
        private void upload(byte[] content, String destFile, int mode) {
            channelSftp = getChannelSftp();
            if (!isDir(destFile)) {
                OutputStream outputStream = null;
                try {
                    if (!exists(destFile) || mode == ChannelSftp.OVERWRITE) {
                        outputStream = channelSftp.put(destFile, ChannelSftp.OVERWRITE);
                    } else {
                        outputStream = channelSftp.put(destFile, mode);
                    }
                    outputStream.write(content);
                    outputStream.flush();
                } catch (SftpException e) {
                    logger.error(String.format("Error when create OutputStream on path %s %s", destFile, e.getCause() + "\n" + e.getMessage()), e);
                    e.printStackTrace();
                } catch (IOException e) {
                    logger.error(String.format("Error when write %s to %s %s", bufferSize + "bytes", destFile, e.getCause() + "\n" + e.getMessage()), e);
                    e.printStackTrace();
                } finally {
                    try {
                        if (null != outputStream) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        logger.error(String.format("Error when close OutputStream for file %s", destFile), e);
                        e.printStackTrace();
                    }
                }
            } else {
                logger.error("Cant put to a dir !");
            }
        }

        /**
         * 下载文件
         *
         * @param sourceFile
         * @param destFile
         * @param mode
         */
        private void downloadFile(String sourceFile, String destFile, int mode) {
            assert (mode == ChannelSftp.OVERWRITE || mode == ChannelSftp.APPEND || mode == ChannelSftp.RESUME);
            channelSftp = getChannelSftp();
            if (exists(sourceFile) && isFile(sourceFile)) {
                try {
                    SftpProgressMonitor monitor = new MySftpProgressMonitor();
                    channelSftp.get(sourceFile, destFile, monitor, ChannelSftp.OVERWRITE);
                } catch (SftpException e) {
                    e.printStackTrace();
                }
            } else {
                logger.error(String.format("SourceFile %s not Exist !", sourceFile));
            }
        }

        /**
         * 下载文件
         *
         * @param sourceFile
         * @param destFile
         * @param overwrite
         */
        public void download(String sourceFile, String destFile, boolean overwrite) {
            download(sourceFile, new File(destFile), overwrite);
        }

        /**
         * 下载文件
         *
         * @param sourceFile
         * @param destFile
         * @param overwrite
         */
        public void download(String sourceFile, File destFile, boolean overwrite) {
            if (isDir(sourceFile)) {
                destFile.mkdirs();
                list(sourceFile).forEach(file -> {
                    File dfile = new File(destFile.getAbsolutePath() + "/" + fileName(file));
                    download(file, dfile, overwrite);
                });
            } else {
                if (destFile.exists() && !overwrite) {
                    logger.warn(String.format("file %s already exists!", destFile.getAbsolutePath()));
                } else {
                    downloadFile(sourceFile, destFile.getAbsolutePath(), ChannelSftp.OVERWRITE);
                }
            }
        }

        /**
         * 远程文件是否存在
         *
         * @param file
         * @return
         */
        public boolean exists(String file) {
            if (StringUtil.isEmpty(file) || "".equals(file)) {
                return false;
            }
            SftpATTRS attrs = getAttr(file);
            return attrs != null;
        }

        /**
         * 是否为目录
         *
         * @param path
         * @return
         */
        public boolean isDir(String path) {
            boolean isdir = false;
            SftpATTRS attrs = getAttr(path);
            if (null != attrs) {
                isdir = attrs.isDir();
            }
            return isdir;
        }

        /**
         * 创建目录
         *
         * @param path
         */
        public void mkdir(String path) {
            channelSftp = getChannelSftp();
            if (!exists(path) && !isDir(path)) {
                try {
                    channelSftp.mkdir(path);
                } catch (SftpException e) {
                    logger.error(String.format("Error when mkdir %s %s", path, e.getCause() + "\n" + e.getMessage()), e);
                    e.printStackTrace();
                }
            } else {
                logger.warn(String.format("Path %s already exists!", path));
            }
        }

        /**
         * 是否是文件
         *
         * @param path
         * @return
         */
        public boolean isFile(String path) {
            SftpATTRS attrs = getAttr(path);
            assert attrs != null;
            boolean ret = attrs.isReg();
//                    !attrs.isBlk() && !attrs.isChr() && !attrs.isFifo() && !attrs.isReg() && !attrs.isSock() && !attrs.isLink();
            return ret;
        }

        /**
         * 获取文件名
         *
         * @param path
         * @return
         */
        public String fileName(String path) {
            String ret = null;
            if (StringUtil.isEmpty(path) || "".equals(path)) {
                return null;
            } else {
                String[] paths = path.split("/");
                ret = paths[paths.length - 1];
                return ret;
            }
        }


        /**
         * 获取文件描述
         *
         * @param file
         * @return
         */
        private SftpATTRS getAttr(String file) {
            if (StringUtil.isEmpty(file) || "".equals(file)) {
                return null;
            }
            channelSftp = getChannelSftp();
            SftpATTRS attrs = null;
            try {
                attrs = channelSftp.stat(file);
            } catch (SftpException e) {
                logger.error(String.format("Error when get info of %s %s", file, e.getCause() + "\n" + e.getMessage()), e);
            }
            return attrs;
        }

        /**
         * 获取下面所有文件
         *
         * @param path
         * @return
         */
        public ArrayList<String> listAll(String path) {
            ArrayList<String> ret = new ArrayList<String>();
            list(path).forEach(x -> {
                if (isDir(x)) {
                    ret.addAll(listAll(x));
                }
                logger.info(x);
                ret.add(x);
            });
            return ret;
        }

        /**
         * 删除
         *
         * @param path
         */
        public boolean delete(String path) {
            AtomicBoolean ret = new AtomicBoolean(true);
            channelSftp = getChannelSftp();
            try {
                list(path).forEach(x -> {
                    if (isDir(x)) {
                        if (!hasChild(x)) {
                            try {
                                channelSftp.rmdir(x);
                                logger.info(String.format("Deleted dir %s", x));
                            } catch (SftpException e) {
                                logger.error(String.format("Error when delet dir %s %s", x, e.getCause() + "\n" + e.getMessage()), e);
                            }
                        } else {
                            delete(x);
                        }
                    } else {
                        try {
                            if (exists(x)) {
                                channelSftp.rm(x);
                                logger.info(String.format("Deleted file %s", x));
                            } else {
                                logger.warn(String.format("%s not exits!", x));
                            }
                        } catch (SftpException e) {
                            logger.error(String.format("Error when delet file %s %s", x, e.getCause() + "\n" + e.getMessage()), e);
                            ret.set(false);
                        }
                    }
                });
                if (exists(path)) {
                    channelSftp.rmdir(path);
                } else {
                    logger.warn(String.format("%s not exits!", path));
                }
            } catch (SftpException e) {
                logger.error(String.format("Error when delete %s %s", path, e.getCause() + "\n" + e.getMessage()), e);
                e.printStackTrace();
                ret.set(false);
            }
            return ret.get();
        }

        /**
         * 下面是否有子文件或子目录
         *
         * @param path 路径
         * @return boolean
         */
        public boolean hasChild(String path) {
            boolean ret = false;
            channelSftp = getChannelSftp();
            if (isDir(path)) {
                ret = !list(path).isEmpty();
            }
            return ret;
        }

        /**
         * 复制文件
         *
         * @param source
         * @param dest
         */
        public void copy(String source, String dest) {
            execCmd(String.format("cp -r %s %s", source, dest));
        }

        public void rename(String source, String dest) {
            channelSftp = getChannelSftp();
            try {
                channelSftp.rename(source, dest);
                logger.info(String.format("Renamed %s to %s", source, dest));
            } catch (SftpException e) {
                logger.error(String.format("Error when rename %s to %s  %s", source, dest, e.getCause() + "\n" + e.getMessage()), e);
                e.printStackTrace();
            }
        }

        /**
         * 移动文件
         *
         * @param source 源路径
         * @param dest   目标路径
         */
        public void move(String source, String dest) {
            execCmd(String.format("mv %s %s", source, dest));
        }

        /**
         * 改变权限
         *
         * @param path 路径
         * @param mod  权限
         */
        public void chmod(String path, int mod) {
            channelSftp = getChannelSftp();
            try {
                channelSftp.chmod(mod, path);
            } catch (SftpException e) {
                e.printStackTrace();
            }
        }


        /**
         * 获取下面文件，depth = 1
         *
         * @param path 路径
         * @return 列表
         */
        public ArrayList<String> list(String path) {
            channelSftp = getChannelSftp();
            ArrayList<String> ret = new ArrayList<String>();
            try {
                String realPath = channelSftp.realpath(path);
                if (!exists(realPath)) {
                    return ret;
                }
                if (isFile(realPath)) {
                    ret.add(realPath);
                    return ret;
                }
                Vector vector = channelSftp.ls(realPath);
                vector.forEach(x -> {
                    ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) x;
                    if (!lsEntry.getFilename().equals("..") && !lsEntry.getFilename().equals(".")) {
                        ret.add(realPath + "/" + lsEntry.getFilename());
                    }
                });
            } catch (SftpException e) {
                logger.error(String.format("Error when ls %s %s", path, e.getCause() + "\n" + e.getMessage()), e);
                e.printStackTrace();
            }
            return ret;
        }

        /**
         * 获取父目录
         *
         * @param realPath 路径
         * @return 路径
         */
        public String getParentDir(String realPath) {
            channelSftp = getChannelSftp();
            StringBuilder parentPath = new StringBuilder();
            if (null == realPath) {
                throw new RuntimeException("Path should not be null");
            }
            if ("".equals(realPath) || "/".equals(realPath)) {
                return "/";
            } else {
                String[] pathSplits = realPath.split("/");
                for (int i = 0; i < pathSplits.length - 1; i++) {
                    parentPath.append(pathSplits[i]).append("/");
                }
                return parentPath.toString();
            }
        }

        /**
         * 改变工作目录
         *
         * @param path 路径
         */
        public void changeDir(String path) {
            channelSftp = getChannelSftp();
            if (isDir(path)) {
                try {
                    channelSftp.cd(path);
                } catch (SftpException e) {
                    logger.error(String.format("Error when cd %s %s", path, e.getCause() + "\n" + e.getMessage()), e);
                    e.printStackTrace();
                }
            } else {
                logger.error("%s is not a dir");
            }
        }

        /**
         * 获取当前工作目录
         *
         * @return
         */
        public String getCurrentPath() {
            channelSftp = getChannelSftp();
            String ret = null;
            try {
                ret = channelSftp.pwd();
            } catch (SftpException e) {
                logger.error(String.format("Error when get PWD %s ", e.getCause() + "\n" + e.getMessage()), e);
                e.printStackTrace();
            }
            return ret;
        }

        /**
         * 获取Channel
         *
         * @return Channel
         */
        private ChannelSftp getChannelSftp() {
            if (null == channelSftp || channelSftp.isEOF() || channelSftp.isClosed()) {
                session = getSession();
                try {
                    channelSftp = (ChannelSftp) session.openChannel("sftp");
                } catch (JSchException e) {
                    logger.error("Error when Create Sftp Channel! " + e.getCause() + "\n" + e.getMessage());
                    e.printStackTrace();
                }
                assert channelSftp != null;
                try {
                    channelSftp.connect(timeout);
                } catch (JSchException e) {
                    logger.error(String.format("Error when Connect to host: %s with port: %s, user: %s, password: %s, timeout: %s", host, port, user, "***", timeout), e);
                    e.printStackTrace();
                }
                return channelSftp;
            } else {
                return channelSftp;
            }
        }

        /**
         * 关闭链接
         */
        public void closeSftpChannel() {
            closeChannel(channelSftp);
        }

        /**
         * 关闭
         *
         * @param channel 通道
         */
        private void closeChannel(Channel channel) {
            if (null != channel && !channel.isEOF() && !channel.isClosed()) {
                try {
                    channel.disconnect();
                } catch (Exception e) {
                    logger.error(String.format("Error when close Channel! %s", e.getCause() + "\n" + e.getMessage()), e);
                    e.printStackTrace();
                }
            } else {
                logger.warn("Channel not active now, need not to close!");
            }
        }
    }

    public class JschLogger implements com.jcraft.jsch.Logger {

        @Override
        public boolean isEnabled(int level) {
            return true;
        }

        @Override
        public void log(int level, String message) {
            logger.info(message);
        }
    }

    public class MySftpProgressMonitor implements SftpProgressMonitor {

        String src;
        String dest;

        long transformed = 0;

        long total = 0;

        float percent = 0f;

        int n = 0;
        int m = 5;

        @Override
        public void init(int op, String src, String dest, long max) {
            logger.info(String.format("Download %s to %s start! max length: %s", src, dest, max));
            this.dest = dest;
            this.src = src;
            total = max;
        }

        @Override
        public boolean count(long count) {
            if (total != 0) {
                percent = transformed / (float) total;
            }
            transformed += count;
            n += 1;
            if (n % m == 0) {
                logger.info(String.format("source file %s total %s, transformed %s, percentage %s", src, total, transformed, percent * 100));
            }
            return true;
        }

        @Override
        public void end() {
            logger.info(String.format("%s transfer finished!", src));
        }
    }

}