package com.nam.struct.bintree

/**
  * Created by Namhwik on 2018/6/25.
  */
trait BinTreePosition extends Position {

  //判断是否有父亲(为使代码描述简洁)//判断是否有父亲(为使代码描述简洁)

  def hasParent: Boolean

  //返回当前节点的父节点
  def getParent: BinTreePosition

  //设置当前节点的父节点
  def setParent(p: BinTreePosition): Unit

  //判断是否为叶子
  def isLeaf: Boolean

  //判断是否为左孩子(为使代码描述简洁)
  def isLChild: Boolean

  //判断是否有左孩子(为使代码描述简洁)
  def hasLChild: Boolean

  //返回当前节点的左孩子
  def getLChild: BinTreePosition

  //设置当前节点的左孩子(注意:this.lChild和c.parent都不一定为空)
  def setLChild(c: BinTreePosition): Unit

  //判断是否为右孩子(为使代码描述简洁)
  def isRChild: Boolean

  //判断是否有右孩子(为使代码描述简洁)
  def hasRChild: Boolean

  //返回当前节点的右孩子
  def getRChild: BinTreePosition

  //设置当前节点的右孩子(注意:this.rChild和c.parent都不一定为空)
  def setRChild(c: BinTreePosition): Unit

  //返回当前节点后代元素的数目
  def getSize: Int

  //是否为空
  def isEmpty:Boolean

  //在孩子发生变化后，更新当前节点及其祖先的规模
  def updateSize(): Unit

  //返回当前节点的高度
  def getHeight: Int

  //在孩子发生变化后，更新当前节点及其祖先的高度
  def updateHeight(): Unit

  //返回当前节点的深度
  def getDepth: Int

  //在父亲发生变化后，更新当前节点及其后代的深度
  def updateDepth(): Unit

  //按照中序遍历的次序，找到当前节点的直接前驱
  def getPrev: BinTreePosition

  //按照中序遍历的次序，找到当前节点的直接后继
  def getSucc: BinTreePosition

  //断绝当前节点与其父亲的父子关系 //返回当前节点
  def secede: BinTreePosition

  //将节点c作为当前节点的左孩子
  def attachL(c: BinTreePosition): BinTreePosition

  //将节点c作为当前节点的右孩子
  def attachR(c: BinTreePosition): BinTreePosition

  //前序遍历
  def elementsPreorder: Iterator[BinTreePosition]

  //中序遍历
  def elementsInorder: Iterator[BinTreePosition]

  //后序遍历
  def elementsPostorder: Iterator[BinTreePosition]

  //层次遍历
  def elementsLevelorder: Iterator[BinTreePosition]
}
