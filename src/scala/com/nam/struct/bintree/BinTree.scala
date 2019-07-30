package scala.com.nam.struct.bintree

/**
  * 二叉树抽象数据类型及其实现
  *
  * Created by Namhwik on 2018/6/25.
  */
trait BinTree {
  //返回树根
  def getRoot(): Unit

  //判断是否树空
  def isEmpty: Boolean

  //返回树的规模(即树根的后代数目)
  def getSize: Int

  //返回树(根)的高度
  def getHeight: Int

  //前序遍历
  def elementsPreorder: Iterator[BinTreePosition]

  //中序遍历
  def elementsInorder: Iterator[BinTreePosition]

  //后序遍历
  def elementsPostorder: Iterator[BinTreePosition]

  //层次遍历
  def elementsLevelorder: Iterator[BinTreePosition]
}
