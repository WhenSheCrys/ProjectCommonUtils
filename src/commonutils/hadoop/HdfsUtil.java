package commonutils.hadoop;

import commonutils.text.StringUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
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
        Pattern pattern = Pattern.compile("^(hdfs)://(([\\d]{1,3}.[\\d]{1,3}.[\\d]{1,3}.[\\d]{1,3})|([1-9a-zA-Z.]+)):([\\d]{1,5})");
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

    public ArrayList<String> list(String path) {
        return list(path, (Objects::nonNull));
    }

    public ArrayList<String> listFiles(String path) {
        return list(path, FileStatus::isFile);
    }

    public ArrayList<String> listDirs(String path) {
        return list(path, FileStatus::isDirectory);
    }

    private ArrayList<String> list(String path, Predicate<FileStatus> fileFilter) {
        ArrayList<String> ret = new ArrayList<>();
        if (!exists(path)) {
            return ret;
        }
        if (isFile(path)) {
            ret.add(getPath(path).toUri().getRawPath());
            return ret;
        }
        try {
            return new ArrayList<>(Arrays.asList(fs.listStatus(getPath(path)))).stream().parallel().filter(fileFilter)
                    .map(x -> x.getPath().toUri().getRawPath()).collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            logger.error("Error when get file list!", e);
        }
        return ret;
    }

    public ArrayList<String> listAll(String path) {
        return listAll(path, Objects::nonNull);
    }

    public ArrayList<String> listAllFiles(String path) {
        return listAll(path, FileStatus::isFile);
    }

    public ArrayList<String> listAllDirs(String path) {
        return listAll(path, FileStatus::isDirectory);
    }

    private ArrayList<String> listAll(String path, Predicate<FileStatus> fileFilter) {
        ArrayList<String> arrayList = new ArrayList<>();
        if (!exists(path)) {
            return arrayList;
        }
        list(path, fileFilter).forEach(x->{
            if (isDirectory(x)) {
                arrayList.addAll(listAll(x, fileFilter));
            }
            arrayList.add(x);
        });
        return arrayList;
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
        String trashPath = "/user/" + user + "/.Trash/";
        if (!exists(trashPath)) {
            mkdir(trashPath);
        }
        for (String s : path) {
            b &= move(s, trashPath);
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

}
