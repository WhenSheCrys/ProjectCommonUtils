package com.nam.struct.bintree
import java.util.Comparator

import scala.reflect.ClassTag

/**
  * Created by Namhwik on 2018/6/27.
  */
trait ElementTrait extends Comparable[ElementTrait]{
  //def defaultCompare:String = this.toString
  //override def compareTo(o: T): Int = if(defaultCompare>o.toString) 1 else 0
  def value : Any
}
