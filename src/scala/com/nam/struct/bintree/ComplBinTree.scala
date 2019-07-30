package scala.com.nam.struct.bintree

/**
  * Created by Namhwik on 2018/6/26.
  */
trait ComplBinTree extends BinTree{

  //生成并返回一个存放e的外部节点，该节点成为新的末节点
  def addLast(e: Any): BinTreePosition

  //删除末节点，并返回其中存放的内容
  def delLast: Any

  //返回按照层次遍历编号为i的节点的位置，0 <= i < size()
  def posOfNode(i: Int): BinTreePosition
}
