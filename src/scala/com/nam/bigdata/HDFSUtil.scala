package scala.com.nam.bigdata
import java.net.URI

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.permission.{FsAction, FsPermission}
import org.apache.hadoop.fs.{FileStatus, FileSystem, FileUtil, Path}
import org.apache.log4j.Logger

import scala.collection.mutable.ArrayBuffer

class HDFSUtil(hdfsBasePath: String) {

  private[this] val logger = Logger.getLogger(this.getClass)

  System.setProperty("HADOOP_USER_NAME", "hdfs")
  System.setProperty("user.name", "hdfs")
  private val fsUri = new URI(hdfsBasePath)
  private val fs = FileSystem.get(fsUri, new Configuration())

  //设置HDFS用户
  def setUser(user: String): Unit = {
    System.setProperty("HADOOP_USER_NAME", user)
    System.setProperty("user.name", user)
  }

  /**
    * 列出目录下所有文件,不包括目录
    *
    * @param path
    * @return
    */
  def listFiles(path: String): Array[String] = {
    fs.listStatus(getPath(path)).map(_.getPath.toUri.getPath)
  }

  /**
    * 列出目录下所有目录
    *
    * @param path
    * @param recursive
    * @return
    */
  def listDirs(path: String, recursive: Boolean = false): Array[String] = {
    val arr = ArrayBuffer[String]()
    val paths = fs.listStatus(getPath(path)).filter(_.isDirectory).map(_.getPath.toUri.getPath)
    if (recursive) {
      paths.foreach { x =>
        listDirs(x, recursive).foreach(arr.append(_))
        arr.append(x)
      }
    } else {
      paths.foreach(arr.append(_))
    }
    arr.toArray
  }

  /**
    * 列出最底层目录
    *
    * @param path
    * @return
    */
  def listBottomDirs(path: String): Array[String] = {
    val dirs = listDirs(path, true)
    dirs.filter { x =>
      listDirs(x, true).length == 0
    }
  }

  /**
    * 列出所有目录
    *
    * @param path      目录
    * @param recursive 递归
    * @return
    */
  def listAllFiles(path: String, recursive: Boolean): Array[String] = {
    val arr = ArrayBuffer[String]()
    val files = fs.listFiles(getPath(path), recursive)
    while (files.hasNext) {
      val f = files.next()
      if (f.isFile)
        arr.append(f.getPath.toUri.getPath)
    }
    arr.toArray
  }

  /**
    * 删除
    *
    * @param paths
    */
  def delete(paths: String*): Unit = {
    paths.par.foreach { path =>
      if (fs.exists(getPath(path))) {
        if (fs.isDirectory(getPath(path))) {
          deleteDir(path)
        } else {
          deleteFile(path)
        }
      } else {
        logger.error(s"$path not exists, SKIP DELETE!")
      }
    }
  }

  /**
    * 删除目录
    *
    * @param paths
    */
  private def deleteDir(paths: String*): Unit = {
    paths.par.foreach { path =>
      if (fs.exists(getPath(path)) && fs.isDirectory(getPath(path))) {
        logger.info(s"try to delete $path")
        val dirs = listDirs(path)
        deleteDir(dirs: _*)
        val files = listAllFiles(path, recursive = false)
        deleteFile(files: _*)
        fs.delete(getPath(path), true)
      } else {
        logger.error(s"$path is not a Dir or not exits")
      }
    }
  }

  /**
    * 删除文件
    *
    * @param paths
    */
  private def deleteFile(paths: String*): Unit = {
    paths.par.foreach { path =>
      if (fs.exists(getPath(path))) {
        if (fs.isFile(getPath(path))) {
          logger.info(s"try to delete $path")
          val res = fs.delete(getPath(path), true)
          if (res) {
            logger.info(s"complete deleting $path")
          } else {
            logger.info(s"an error occurred during deleting $path")
          }
        } else {
          logger.warn(s"$path is not a file! SKIP DELETE!")
        }
      } else {
        logger.warn(s"$path not exits! SKIP DELETE!")
      }
    }
  }

  /**
    * 删除到回收站
    *
    * @param paths
    */
  def moveToTrash(paths: String*): Unit = {
    val hadoop_user_name = System.getProperty("HADOOP_USER_NAME").trim
    val user_name = System.getProperty("user.name").trim
    val user = if (hadoop_user_name.isEmpty) {
      if (user_name.isEmpty) {
        "hdfs"
      } else {
        user_name
      }
    } else {
      hadoop_user_name
    }
    val trashPath = s"/user/$user/.Trash"
    paths.par.foreach { path =>
      move(path, trashPath)
    }
  }

  /**
    * 移动
    *
    * @param source
    * @param dest
    */
  def move(source: String, dest: String): Unit
  = {
    if (!exists(source)) {
      logger.error(s"Sourc path:$source not exists!")
    } else {
      copy(source, dest, deleteSourceFile = true)
      logger.info(s"moved $source to $dest!")
    }
  }

  /**
    * 复制
    *
    * @param source           源地址
    * @param destination      目标地址
    * @param deleteSourceFile 是否删除源文件
    * @param overWrite        是否覆盖
    * @return
    */
  def copy(source: String, destination: String, deleteSourceFile: Boolean = false, overWrite: Boolean = false): Unit = {
    val sourceFile = getPath(source)
    val destFile = getPath(destination)

    if (fs.exists(sourceFile)) {
      val sourceDir = new FileStatus()
      sourceDir.setPath(sourceFile)
      if (!fs.exists(destFile)) {
        FileUtil.copy(fs, sourceDir, fs, destFile, deleteSourceFile, overWrite, fs.getConf)
      } else {
        if (fs.isDirectory(sourceFile) && fs.isFile(destFile)) {
          logger.error("can't copy a directory to a file!", new RuntimeException("can't copy a directory to a file!"))
        } else if (fs.isFile(sourceFile) && fs.isFile(destFile)) {
          logger.error(s"destination:$destination already exists!", new RuntimeException(s"destination:$destination already exists!"))
        } else {
          FileUtil.copy(fs, sourceDir, fs, destFile, deleteSourceFile, overWrite, fs.getConf)
        }
      }
    } else {
      logger.error(s"source:$source not exists!", new RuntimeException(s"source:$source not exists!"))
    }

  }

  /**
    * 获取HDFS地址
    *
    * @param path
    * @return
    */
  def getPath(path: String): Path = {
    //    if (path.toLowerCase().startsWith("hdfs://")) {
    //      new Path(path)
    //    } else {
    //      new Path(hdfsBasePath + path)
    //    }
    new Path(path)
  }

  /**
    * 改变所属用户
    *
    * @param path      路径
    * @param group     所属组
    * @param owner     所属用户
    * @param recursive 是否递归
    */
  def chown(path: String, group: String, owner: String, recursive: Boolean = true): Unit = {
    val p = getPath(path)
    val oldGroup = fs.getFileStatus(p).getGroup
    val oldOwner = fs.getFileStatus(p).getOwner

    if (group.equals(oldGroup) && owner.equals(oldOwner)) {
      logger.warn("组名和用户名未改变")
    } else {
      try {
        fs.setOwner(p, owner, group)
        if (recursive && fs.getFileStatus(p).isDirectory) {
          listDirs(path).par.foreach(p1 => chown(p1, group, owner, recursive))
          listAllFiles(path, recursive = false).par.foreach(p1 => chown(p1, group, owner, recursive = false))
        }
        logger.info(s"changed owner for [$path]")
      } catch {
        case e: Exception =>
          e.printStackTrace()
      }
    }
  }

  /**
    * 改变权限
    *
    * 0 无
    * 1 可执行
    * 2 写
    * 4 读
    * 3 可执行 + 写
    * 5 可执行 + 读
    * 6 读 + 写
    * 7 读 + 写 + 可执行
    *
    * @param path            地址
    * @param userPermission  所属用户权限
    * @param groupPermission 所属组权限
    * @param otherPermission 其他用户权限
    * @param recursive       是否递归
    */
  def chmod(path: String, userPermission: Int, groupPermission: Int, otherPermission: Int, recursive: Boolean): Unit = {

    require(userPermission <= 7 && userPermission > 0
      && groupPermission <= 7 && groupPermission > 0
      && otherPermission <= 7 && otherPermission > 0, "Invalid permission!")

    /**
      * 根据数字获取权限信息
      *
      * @param p
      * @return
      */
    def getPermission(p: Int): FsAction = {
      p match {
        case 1 => FsAction.EXECUTE
        case 2 => FsAction.WRITE
        case 3 => FsAction.WRITE_EXECUTE
        case 4 => FsAction.READ
        case 5 => FsAction.READ_EXECUTE
        case 6 => FsAction.READ_WRITE
        case 7 => FsAction.ALL
        case _ => FsAction.NONE
      }
    }

    if (fs.isDirectory(getPath(path)) && recursive) {
      val dirs = listDirs(path)
      if (dirs.length > 0) {
        dirs.par.foreach { p =>
          chmod(p, userPermission, groupPermission, otherPermission, recursive)
        }
      }
      val files = listAllFiles(path, recursive = false)
      if (files.length > 0) {
        files.par.foreach { p =>
          chmod(p, userPermission, groupPermission, otherPermission, recursive)
        }
      }
    }

    val m: FsPermission = new FsPermission(getPermission(userPermission), getPermission(groupPermission), getPermission(otherPermission))

    fs.setPermission(getPath(path), m)
  }

  /**
    * 改变权限
    *
    * @param path      路径
    * @param mod       权限 形如 "777", "766", "664" 的形式
    * @param recursive 递归
    */
  def chmod(path: String, mod: String, recursive: Boolean = true): Unit = {
    require(mod.length > 0 && mod.length <= 3, s"Invalid permission number, required 3 but found ${mod.length}!")
    mod.foreach { x =>
      val c = x.toInt
      require(c >= 48 && c <= 55, s"Invalid permisson at number $x")
    }
    val arr = mod.split("")
    val u = arr(0).toInt
    val g = arr(1).toInt
    val o = arr(2).toInt
    chmod(path, u, g, o, recursive)
  }

  /**
    * 创建目录
    *
    * @param path
    * @return
    */
  def mkDir(path: String): Unit = {
    if (fs.exists(getPath(path))) {
      if (fs.isDirectory(getPath(path))) {
        logger.error(s"$path already exits")
      } else {
        logger.warn(s"already has a file named $path! but we created a directory.")
        fs.mkdirs(getPath(path))
      }
    } else {
      fs.mkdirs(getPath(path))
    }
  }

  /**
    * 创建文件
    *
    * @param path
    * @return
    */
  def createFile(path: String): Unit = {
    if (fs.exists(getPath(path))) {
      if (fs.isFile(getPath(path))) {
        logger.error(s"$path already exits")
      } else {
        fs.createNewFile(getPath(path))
      }
    } else {
      fs.createNewFile(getPath(path))
    }
  }

  /**
    * 重命名
    *
    * @param path1 原地址
    * @param path2 目标地址
    */
  def rename(path1: String, path2: String): Unit = {
    fs.rename(getPath(path1), getPath(path2))
  }

  /**
    * 地址是否存在
    *
    * @param path
    * @return
    */
  def exists(path: String): Boolean = {
    fs.exists(getPath(path))
  }

  def close: Unit = {
    fs.close()
  }



}