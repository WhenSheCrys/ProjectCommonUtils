package commonutils.struct.bintree

import scala.collection.mutable

/**
  * Created by Namhwik on 2018/6/25.
  */
case class BinTreeNode(var element: Any, var parent: BinTreePosition = null, asLChild: Boolean = false,
                       var lChild: BinTreePosition = null, var rChild: BinTreePosition = null)
  extends BinTreePosition {

  protected var size: Int = 1 //后代数目
  protected var height: Int = 0 //高度
  protected var depth: Int = 0 //深度


  override def hasParent: Boolean = parent != null

  override def getParent: BinTreePosition = parent

  override def setParent(p: BinTreePosition): Unit = parent = p

  override def isLeaf: Boolean = lChild == null && rChild == null && parent != null

  override def isLChild: Boolean = asLChild

  override def hasLChild: Boolean = lChild != null

  override def getLChild: BinTreePosition = lChild

  override def setLChild(c: BinTreePosition): Unit = lChild = c

  override def isRChild: Boolean = !asLChild

  override def hasRChild: Boolean = rChild != null

  override def getRChild: BinTreePosition = rChild

  override def setRChild(c: BinTreePosition): Unit = rChild = c

  override def getSize: Int = size

  override def updateSize(): Unit = {
    size = 1
    if (hasLChild) size += getLChild.getSize
    if (hasRChild) size += getRChild.getSize
    if (hasParent) getParent.updateSize()
  }

  override def getHeight: Int = height

  override def updateHeight(): Unit = {
    height = 0
    if (hasLChild) height = math.max(height, 1 + getLChild.getHeight)
    if (hasRChild) height = math.max(height, 1 + getRChild.getHeight)
    if (hasParent) getParent.updateHeight()

  }

  override def getDepth: Int = depth

  override def updateDepth(): Unit = {
    depth = if (hasParent) 1 + getParent.getDepth else 0
    if (hasLChild) getLChild.updateDepth()
    if (hasRChild) getRChild.updateDepth()
  }


  //按照中序遍历的次序，找到当前节点的直接前驱
  override def getPrev: BinTreePosition = {
    if (hasLChild)
      findMaxDescendant(getLChild)
    else {
      if (isRChild)
        getParent
      else {
        //是左叶子节点
        var node: BinTreePosition = this
        while (node.isLChild)
          node = node.getParent
        node.getParent
      }
    }

  }

  //按照中序遍历的次序，找到当前节点的直接后继
  override def getSucc: BinTreePosition = {
    if (hasRChild)
      findMinDescendant(getRChild)
    else {
      if (isLChild)
        getParent
      else {
        //是右叶子节点
        var node: BinTreePosition = this
        while (node.isRChild) {
          node = node.getParent
        }
        node.getParent
      }
    }
  }

  override def secede: BinTreePosition = {
    if (null != parent) {
      if (isLChild)
        parent.setLChild(null) //切断父亲指向当前节点的引用
      else
        parent.setRChild(null)
      parent.updateSize() //更新当前节点及其祖先的规模
      parent.updateHeight(); //更新当前节点及其祖先的高度
      parent = null //切断当前节点指向原父亲的引用
      updateDepth() //更新节点及其后代节点的深度
    }
    this //返回当前节点
  }


  override def attachL(c: BinTreePosition): BinTreePosition = {
    if (hasLChild) getLChild.secede
    if (null != c) {
      setLChild(c)
      c.updateDepth()
      updateHeight()
      updateSize()
    }
    this
  }

  override def attachR(c: BinTreePosition): BinTreePosition = {
    if (hasRChild) getRChild.secede
    if (null != c) {
      setRChild(c)
      c.updateDepth()
      updateHeight()
      updateSize()
    }
    this
  }

  override def elementsPreorder: Iterator[BinTreePosition] = {
    // val list = mutable.DoubleLinkedList[Any]
    val list = new java.util.LinkedList[BinTreePosition]
    preorder(list, this)
    import scala.collection.JavaConversions._
    list.toIterator
  }

  override def elementsInorder: Iterator[BinTreePosition] = {
    val list = new java.util.LinkedList[BinTreePosition]
    elementsInorder(list, this)
    import scala.collection.JavaConversions._
    list.toIterator
  }

  override def elementsPostorder: Iterator[BinTreePosition] = {
    val list = new java.util.LinkedList[BinTreePosition]
    elementsPostorder(list, this)
    import scala.collection.JavaConversions._
    list.toIterator
  }

  override def elementsLevelorder: Iterator[BinTreePosition] = {
    val list = new java.util.LinkedList[BinTreePosition]
    elementsLevelorder(list, this)
    import scala.collection.JavaConversions._
    list.toIterator
  }

  override def getElem: Any = element

  override def setElem(element: Any): Unit = this.element=element


  //在v的后代中，找出最小者
  protected def findMinDescendant(v: BinTreePosition): BinTreePosition = {
    var node = v
    if (null != node) //从v出发，沿左孩子链一直下降
      while (node.hasLChild) node = node.getLChild // 至此，v或者为空，或者没有左孩子
    node
  }

  //在v的后代中，找出最大者
  protected def findMaxDescendant(v: BinTreePosition): BinTreePosition = {
    var node = v
    if (null != node) //从v出发，沿左孩子链一直下降 //至此，v或者为空，或者没有左孩子
      while (node.hasRChild)
        node = node.getRChild
    node
  }

  //前序遍历
  def preorder(list: java.util.List[BinTreePosition], binTreePosition: BinTreePosition): Unit = {
    if (null != binTreePosition) {
      list.add(binTreePosition)
      //遍历左子树
      preorder(list, binTreePosition.getLChild)
      //遍历右子树
      preorder(list, binTreePosition.getRChild)
    }
  }

  //中序遍历
  def elementsInorder(list: java.util.List[BinTreePosition], binTreePosition: BinTreePosition): Unit = {
    if (null != binTreePosition) {
      elementsInorder(list, binTreePosition.getLChild)
      list.add(binTreePosition)
      elementsInorder(list, binTreePosition.getRChild)
    }
  }

  //后序遍历
  def elementsPostorder(list: java.util.List[BinTreePosition], binTreePosition: BinTreePosition): Unit = {
    if (null != binTreePosition) {
      elementsPostorder(list, binTreePosition.getLChild)
      list.add(binTreePosition)
      elementsPostorder(list, binTreePosition.getRChild)
    }
  }

  //层次遍历
  def elementsLevelorder(list: java.util.List[BinTreePosition], binTreePosition: BinTreePosition): Unit = {
    val queue = new mutable.Queue[BinTreePosition]
    queue.enqueue(binTreePosition)
    while (queue.nonEmpty) {
      val bts = queue.dequeue()
      list.add(bts)
      if (bts.hasLChild) queue.enqueue(bts.getLChild)
      if (bts.hasRChild) queue.enqueue(bts.getRChild)
    }
  }

}
