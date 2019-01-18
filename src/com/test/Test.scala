package com.test

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Created by Namhwik on 2018/6/26.
  */
object Test {
  def main(args: Array[String]): Unit = {
    val list=new ArrayBuffer[String]()
    list.append("1111")
    val list1=new ArrayBuffer[String]()
    list1.append("1111")
    println(list.hashCode())
    println(list.mkString(","))
    println(list1.hashCode())
    println(list1.mkString(","))

    val map = mutable.Map[ArrayBuffer[String],String]()
    map.put(list,"1")
    map.put(list1,"2")
    println(map.size)
    list.sorted
  }
}
