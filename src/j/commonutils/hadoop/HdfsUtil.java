package j.commonutils.hadoop;

import j.commonutils.text.Regex;
import j.commonutils.text.StringUtil;
import j.commonutils.text.UnitUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HdfsUtil {
    private String url;
    private String user = "hdfs";
    private FileSystem fs;
    private final Logger logger = LogManager.getLogger(this.getClass());

    public HdfsUtil(String url) {
        assert StringUtil.isNotBlank(url);
        this.url = url;
        init();
    }

    public HdfsUtil(String url, String user) {
        assert StringUtil.isNotBlank(url) && StringUtil.isNotBlank(user);
        this.url = url;
        this.user = user;
        init();
    }

    private void init() {
        assert url.startsWith("hdfs://") || url.startsWith("/");
        Pattern pattern = Regex.HDFS_PATH_REGEX;
        Matcher matcher = pattern.matcher(url);
        if (matcher.matches()) {
            url = matcher.group(0);
        } else {
            throw new IllegalArgumentException("Base hdfs url is invalid!");
        }
        System.setProperty("HADOOP_USER_NAME", user);
        System.setProperty("user.name", user);
        try {
            this.fs = FileSystem.get(new URI(url), new Configuration(true));
        } catch (IOException e) {
            logger.error("Error when initialize hdfs file system!", e);
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String[] list(String path) {
        return list(path, (Objects::nonNull));
    }

    public String[] listFiles(String path) {
        return list(path, FileStatus::isFile);
    }

    public String[] listDirs(String path) {
        return list(path, FileStatus::isDirectory);
    }

    private String[] list(String path, Predicate<FileStatus> fileFilter) {
        ArrayList<String> ret = new ArrayList<>();
        if (exists(path)) {
            if (isDirectory(path)) {
                try {
                    ret.addAll(Arrays.stream(fs.listStatus(getPath(path))).parallel().filter(fileFilter)
                            .map(x -> x.getPath().toUri().getRawPath()).collect(Collectors.toCollection(ArrayList::new)));
                } catch (IOException e) {
                    logger.error("Error when get file list!", e);
                }
            }
        }
        return ret.toArray(new String[0]);
    }

    public String[] listAll(String path) {
        ArrayList<String> ret = new ArrayList<>();
        for (String s : list(path)) {
            ret.add(s);
            if (isDirectory(s)) {
                ret.addAll(Arrays.asList(listAll(s)));
            }
        }
        return ret.toArray(new String[0]);
    }

    public String[] listAllFiles(String path) {
        ArrayList<String> ret = new ArrayList<>();
        for (String s : list(path)) {
            if (isFile(s)) {
                ret.add(s);
            } else {
                ret.addAll(Arrays.asList(listAllFiles(s)));
            }
        }
        return ret.toArray(new String[0]);
    }

    public String[] listAllDirs(String path) {
        ArrayList<String> ret = new ArrayList<>();
        for (String s : list(path)) {
            if (isDirectory(s)) {
                ret.add(s);
                ret.addAll(Arrays.asList(listAllDirs(s)));
            }
        }
        return ret.toArray(new String[0]);
    }

    public boolean delete(String... path) {
        assert null != path && path.length > 0 : "Paths should not be empty or null!";
        boolean ret = true;
        for (String s : path) {
            try {
                ret &= fs.delete(getPath(s), true);
            } catch (IOException e) {
                logger.error("Error when delete " + s, e);
            }
        }
        return ret;
    }

    public boolean exists(String path) {
        boolean exits = false;
        try {
            exits = fs.exists(getPath(path));
        } catch (IOException e) {
            logger.error(e);
        }
        return exits;
    }

    public boolean createFile(String path) {
        boolean created = false;
        if (!exists(path)) {
            try {
                created = fs.createNewFile(getPath(path));
            } catch (IOException e) {
                logger.error("Error when create file " + path, e);
            }
        }
        return created;
    }

    public boolean mkdir(String path) {
        boolean created = false;
        if (!exists(path)) {
            try {
                created = fs.mkdirs(getPath(path));
            } catch (IOException e) {
                logger.error("Error when create file " + path, e);
            }
        }
        return created;
    }

    public boolean chown(String user, String group, String... path) {
        boolean ch = true;
        for (String s : path) {
            try {
                if (StringUtil.isBlank(group)) {
                    fs.setOwner(getPath(s), user, getFileStatus(s).getGroup());
                } else {
                    fs.setOwner(getPath(s), user, group);
                }
            } catch (Exception e) {
                ch = false;
                logger.error("Error when chown " + s, e);
            }
        }
        return ch;
    }

    public boolean chown(String user, String... path) {
        return chown(user, null, path);
    }

    private FileStatus getFileStatus(String path) {
        FileStatus ret = null;
        try {
            ret = fs.getFileStatus(getPath(path));
        } catch (IOException e) {
            logger.error("Error when get file status: " + path, e);
        }
        return ret;
    }

    public boolean rename(String sourcePath, String destPath) {
        boolean ret = false;
        try {
            ret = fs.rename(getPath(sourcePath), getPath(destPath));
        } catch (IOException e) {
            logger.error("Error when rename " + sourcePath + " to " + destPath, e);
        }
        return ret;
    }

    public boolean copy(String sourcePath, String destPath, boolean overwrite) {
        boolean c = false;
        try {
            c = FileUtil.copy(fs, getPath(sourcePath), fs, getPath(destPath), false, overwrite, new Configuration());
        } catch (IOException e) {
            logger.error("Error when copy " + sourcePath + " to " + destPath, e);
        }
        return c;
    }

    public boolean move(String sourcePath, String destPath) {
        return rename(sourcePath, destPath);
    }

    public boolean moveToTrash(String... path) {
        boolean b = true;
        for (String s : path) {
            Path trashPath = fs.getTrashRoot(getPath(s));
            b &= move(s, trashPath.toString());
        }
        return b;
    }

    public boolean chmod(String path, String mod) {
        boolean b = false;
        try {
            b = FileUtil.chmod(path, mod, true) == 0;
        } catch (IOException e) {
            logger.error("Error when chmod " + path, e);
        }
        return b;
    }

    public boolean put(String sourcePath, String destPath) {
        boolean b = false;
        try {
            fs.copyFromLocalFile(getPath(sourcePath), getPath(destPath));
            b = true;
        } catch (IOException e) {
            logger.error("Error when copy from " + sourcePath + " to " + destPath, e);
        }
        return b;
    }

    public boolean get(String sourcePath, String destPath) {
        boolean b = false;
        try {
            fs.copyToLocalFile(getPath(sourcePath), getPath(destPath));
            b = true;
        } catch (IOException e) {
            logger.error("Error when copy from " + sourcePath + " to " + destPath, e);
        }
        return b;
    }

    public boolean isFile(String path) {
        return getFileStatus(path).isFile();
    }

    public boolean isDirectory(String path) {
        return getFileStatus(path).isDirectory();
    }

    private Path getPath(String path) {
        return new Path(path);
    }

    public long getSize(String path) {
        return getFileStatus(path).getLen();
    }

    public String getTotalSizeAsHumanReadable(String... paths) {
        long totalSize = 0L;
        for (String path : paths) {
            if (isFile(path)) {
                totalSize += getSize(path);
            } else {
                for (String s : listAllFiles(path)) {
                    totalSize += getSize(s);
                }
            }
        }
        return UnitUtil.ByteUtil.toString(totalSize, true);
    }

    public boolean saveObject(Object o, String path, boolean overwrite) {
        boolean res = false;
        try {
            FSDataOutputStream dataOutputStream = fs.create(getPath(path), overwrite);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(dataOutputStream.getWrappedStream());
            objectOutputStream.writeObject(o);
            objectOutputStream.flush();
            dataOutputStream.close();
            objectOutputStream.close();
            res = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public Object readObject(String path) {
        Object res = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(fs.open(getPath(path)));
            res = objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }

}
