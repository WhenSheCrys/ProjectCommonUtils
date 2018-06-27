package commonutils.struct.bintree

/**
  * Created by Namhwik on 2018/6/26.
  */
class BinTree_LinkedList extends BinTree {
  private var root:BinTreePosition = _
  override def getRoot(): BinTreePosition = root

  override def isEmpty: Boolean = null==root

  override def getSize: Int = if(isEmpty) 0 else root.getSize

  override def getHeight: Int = if(isEmpty) -1 else root.getHeight

  override def elementsPreorder: Iterator[BinTreePosition] = if(isEmpty) null else root.elementsPreorder

  override def elementsInorder: Iterator[BinTreePosition] = if(isEmpty) null else root.elementsInorder

  override def elementsPostorder: Iterator[BinTreePosition] = if(isEmpty) null else root.elementsPostorder

  override def elementsLevelorder: Iterator[BinTreePosition] = if(isEmpty) null else root.elementsLevelorder
}
