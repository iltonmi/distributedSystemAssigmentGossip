package com.company;

/**
 * 传播节点类
 * @author lin weili
 */
public class Node{
    /**
     * 节点值
     */
    private double val;

    /**
     * 节点下标
     */
    private int index;

    /**
     * 节点是否被隔离
     */
    private boolean removed = false;

    /**
     * 节点是否被感染
     */
    private boolean infected = false;

    /**
     * K值
     */
    private double k;

    /**
     * 节点传播兴趣
     */
    private double interest = 1.0;

    /**
     * 节点当前传播轮数
     */
    private int round = 0;

    public Node(int index, boolean infected, double val, boolean removed, double k) {
        this.index = index;
        this.infected = infected;
        this.val = val;
        this.removed = removed;
        this.k = k;
        this.interest = 1 / k;
    }

    public double getVal() {
        return val;
    }

    public void setVal(double val) {
        this.val = val;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public boolean isInfected() {
        return infected;
    }

    public void setInfected(boolean infected) {
        this.infected = infected;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    @Override
    public String toString() {
        return "Node{" +
                "val=" + val +
                ", index=" + index +
                ", removed=" + removed +
                ", infected=" + infected +
                ", k=" + k +
                ", interest=" + interest +
                ", round=" + round +
                '}';
    }
}
