package com.test;

import com.nam.struct.bintree.BinTreePosition;

/**
 * Created by Namhwik on 2018/6/28.
 */
public class JavaTest {
    //通过旋转，使节点z的平衡因子的绝对值不超过1(支持AVL树) //返回新的子树根
    public static BinTreePosition rotate(BinTreePosition z) {
        BinTreePosition y = tallerChild(z);//取y为z更高的孩子
        BinTreePosition x = tallerChild(y);//取x为y更高的孩子
        boolean cType = z.isLChild();//记录:z是否左孩子
        BinTreePosition p = z.getParent();//p为z的父亲
        BinTreePosition a, b, c;//自左向右，三个节点
        BinTreePosition t0, t1, t2, t3;//自左向右，四棵子树
        /******** 以下分四种情况 ********/
        if (y.isLChild()) {//若y是左孩子，则
            c = z; t3 = z.getRChild();
            if (x.isLChild()) {
//若x是左孩子
                b = y;
                t2 = y.getRChild();
                a = x; t1 = x.getRChild();
                t0
            } else {//若x是右孩子
                a = y; t0 = y.getLChild();
                b = x; t1 = x.getLChild();
                t2
            }
        } else {//若y是右孩子，则
            a = z;
            t0 = z.getLChild();
            if (x.isRChild()) {//若x是右孩子
                b = y;
                t1 = y.getLChild();
                c = x; t2 = x.getLChild();
                t3 } else {//若x是左孩子
                c = y;
                t3 = y.getRChild();
                b = x;
                t1 = x.getLChild();
                t2 = x.getLChild();
= (BSTreeNode)x.getRChild();
= (BSTreeNode)x.getRChild();
= (BSTreeNode)x.getRChild();
            } }
        //摘下三个节点
        z.secede();
        y.secede();
        x.secede();
//摘下四棵子树
        if (null != t0) t0.secede();
        if (null != t1) t1.secede();
        if (null != t2) t2.secede();
        if (null != t3) t3.secede();
//重新链接
        a.attachL(t0); a.attachR(t1); b.attachL(a);
        c.attachL(t2); c.attachR(t3); b.attachR(c);
//子树重新接入原树
        if (null != p)
            if (cType)  p.attachL(b);
            else        p.attachR(b);
        return b;
//返回新的子树根
    }//rotate
}
