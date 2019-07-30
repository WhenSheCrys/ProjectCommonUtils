package scala.com.nam.struct.bintree

import scala.collection.mutable
import scala.util.control.Breaks._

/**
  * Created by Namhwik on 2018/6/25.
  */
case class BinTreeNode(var element: ElementTrait, var parent: BinTreePosition = null, asLChild: Boolean = false,
                       var lChild: BinTreePosition = null, var rChild: BinTreePosition = null)
  extends BinTreePosition {

  protected var size: Int = 1 //后代数目
  protected var height: Int = 0 //高度
  protected var depth: Int = 0 //深度

  override def isEmpty: Boolean = size < 2

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

  override def getElem: ElementTrait = element

  override def setElem(element: ElementTrait): Unit = this.element = element


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

  //二分查找
  def binSearch(k: BinTreePosition, v: ElementTrait): BinTreePosition = {
    var node = k
    breakable {
      while (true) {
        if (node.getElem.compareTo(v) < 0) {
          if (node.hasLChild)
            node = node.getLChild
          else
            break
        } else if (node.getElem.compareTo(v) > 0) {
          if (node.hasRChild)
            node = node.getRChild
          else
            break
        }
        else
          break
      }
    }
    node
  }

  //插入节点
  def insert(root: BinTreePosition, elem: ElementTrait): BinTreePosition = {
    if (root.isEmpty) {
      if (root.getElem.compareTo(elem) > 0)
        root.attachR(BinTreeNode(elem, this))
      else
        root.attachL(BinTreeNode(elem, this, asLChild = true))
    } else {
      var node = root
      while (true) {
        val nearestNode = binSearch(node, elem)
        nearestNode.getElem.compareTo(elem) match {
          case 1 => node.attachL(BinTreeNode(elem, nearestNode, asLChild = true))
          case -1 => node.attachR(BinTreeNode(elem, node))
          case 0 => if (!node.hasLChild)
            node.attachL(BinTreeNode(elem, node, asLChild = true))
          else if (!node.hasRChild)
            node.attachR(BinTreeNode(elem, node))
          else
            node = node.getLChild
          case _ => throw new RuntimeException("节点比较异常")
        }
      }
    }
    root
  }

  //删除节点
  def remove(root: BinTreePosition, elem: ElementTrait): BinTreePosition = {
    val nearNode = binSearch(root, elem)
    if (nearNode.getElem.compareTo(elem) != 0)
      null
    else {
      if (!nearNode.hasLChild) {
        nearNode.secede
        if (nearNode.hasRChild) nearNode.getParent.attachL(nearNode.getRChild)
      }
      else {
        val preNode = nearNode.getPrev
        swap(nearNode, preNode)
        preNode.secede
      }
      root
    }
  }

  //交换节点
  def swap(a: BinTreePosition, b: BinTreePosition): Unit = {
    val a_elem = a.getElem
    a.setElem(b.getElem)
    b.setElem(a_elem)
  }


  def rebance(root: BinTreePosition, z: BinTreePosition): BinTreePosition = {
    if (z == null) root
    else {
      var node = z
      breakable {
        while (true) {
          if (!isBalanced(node)) rorate(node)
          if (!node.hasParent) break
          node = node.getParent
        }
      }
      node
    }
  }

  //判断节点v是否平衡
  protected def isBalanced(v: BinTreePosition): Boolean = {
    if (null == v) return true
    val lH = if (v.hasLChild) v.getLChild.getHeight else -1
    val rH = if (v.hasRChild) v.getRChild.getHeight else -1
    val deltaH = lH - rH
    (-1 <= deltaH) && (deltaH <= 1)
  }


  //通过旋转，使节点z的平衡因子的绝对值不超过1(支持AVL树) //返回新的子树根
  def rorate(z: BinTreePosition): BinTreePosition = {
    val y = tallerChild(z)
    val x = tallerChild(y)
    val cType = z.isLChild
    val p = z.getParent //p为z的父亲
    var a, b, c: BinTreePosition = null //自左向右，三个节点
    var t0, t1, t2, t3: BinTreePosition = null //自左向右，四棵子树
    /** ****** 以下分四种情况 ********/
    if (y.isLChild) {
      //若y是左孩子，则
      c = z
      t3 = z.getRChild
      if (x.isLChild) {
        //若x是左孩子
        b = y
        t2 = y.getRChild
        a = x
        t1 = x.getRChild
        t0 = x.getLChild
      } else {
        //若x是右孩子
        a = y
        t0 = y.getLChild
        b = x
        t1 = x.getLChild
        t2 = x.getRChild
      }
    } else {
      //若y是右孩子，则
      a = z
      t0 = z.getLChild
      if (x.isRChild) {
        //若x是右孩子
        b = y
        t1 = y.getLChild
        c = x
        t2 = x.getLChild
        t3 = x.getRChild
      } else {
        //若x是左孩子
        c = y
        t3 = y.getRChild
        b = x
        t1 = x.getLChild
        t2 = x.getRChild
      }
    }

    //摘下三个节点
    z.secede
    y.secede
    x.secede
    //摘下四棵子树
    if (null != t0) t0.secede
    if (null != t1) t1.secede
    if (null != t2) t2.secede
    if (null != t3) t3.secede
    //重新链接
    a.attachL(t0)
    a.attachR(t1)
    b.attachL(a)
    c.attachL(t2)
    c.attachR(t3)
    // 子树重新接入原树
    if (null != p)
      if (cType) p.attachL(b) else p.attachR(b)
    b
  }

  //返回节点p的孩子中的更高者
  protected def tallerChild(v: BinTreePosition): BinTreePosition = {
    val lH = if (v.hasLChild) v.getLChild.getHeight
    else -1
    val rH = if (v.hasRChild) v.getRChild.getHeight
    else -1
    if (lH > rH) return v.getLChild
    if (lH < rH) return v.getRChild
    if (v.isLChild) v.getLChild
    else v.getRChild
  }

  // 返回节点p的孩子中的更矮者
  protected def shorterChild(v: BinTreePosition): BinTreePosition = {
    val lH = if (v.hasLChild) v.getLChild.getHeight
    else -1
    val rH = if (v.hasRChild) v.getRChild.getHeight
    else -1
    if (lH > rH) return v.getRChild
    if (lH < rH) return v.getLChild
    if (v.isLChild) v.getRChild
    else v.getLChild
  }

}
