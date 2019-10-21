package com.company;

import java.util.Random;
import java.util.concurrent.Executor;

/**
 * 感染者的Runnable
 * @author lin weili
 */

public class Gossiper implements Runnable {

    /**
     * 此线程持有的节点
     */
    private Node node;

    /**
     * 包含全部节点的容器
     */
    private Node[] nodes;

    /**
     * 误差限
     */
    private double error_limit;

    /**
     * 此线程所在的线程池
     */
    private Executor exec;

    public Gossiper(Node node, Node[] nodes, Executor exec,double error_limit) {
        this.node = node;
        this.nodes = nodes;
        this.error_limit = error_limit;
        this.exec = exec;
    }

    @Override
    public void run() {
        //开始感染前，设置此线程的初始感染轮数
        //若线程是因为被节点P感染而启动的，则此线程的初始感染轮数为启动此线程是节点P的感染轮数
        GossipService.setRound(node.getRound());


        while (true){
            if (node.isRemoved()){
                //若此节点被隔离，则设置此节点的轮数为当前线程轮数，并退出循环
                node.setRound(GossipService.getRound());
                return;
            }
            //随机选取将要被感染的节点下标
            int toIndex = new Random().nextInt(nodes.length);
            //若选中了自己，则跳过此次循环
            if (toIndex == node.getIndex()){
                continue;
            }
            //获取被感染节点
            Node toNode = nodes[toIndex];
            int fromIndex = node.getIndex();
            //根据Java并发编程实战
            //因为两个Node的index不可能相等,可以保证获取锁的顺序从而防止互相感染时发生死锁
            if (fromIndex < toIndex){
                synchronized (node){
                    synchronized (toNode){
                        communicate(node, toNode);;
                    }
                }
            }else{
                synchronized (toNode){
                    synchronized (node){
                        communicate(node, toNode);;
                    }
                }
            }
        }
    }

    /**
     * 节点通信，信息从{@code fromNode} 发送到 {@code toNode}
     * @param fromNode 感染者
     * @param toNode 被感染者
     * @return 是否感染成功
     */
    private boolean communicate(final Node fromNode, final Node toNode){
        //当被感染者处于已感染的状态，并且两者之差的绝对值小于误差限，感染者有(1 - node.getInterest())的概率自我隔离
        if (toNode.isInfected() && Math.abs(fromNode.getVal() - toNode.getVal()) < error_limit && node.getInterest() < new Random().nextDouble()){
            node.setRemoved(true);
            return false;
        }
        //若被感染者处于已感染的状态，并且两者之差的绝对值小于误差限，但是感染者未被隔离，
        // 那么它感染下一个节点的兴趣减少
        if (Math.abs(fromNode.getVal() - toNode.getVal()) < error_limit){
            fromNode.setInterest(fromNode.getInterest() / fromNode.getK());
        }
        double average = (fromNode.getVal() + toNode.getVal()) / 2;
        fromNode.setVal(average);
        toNode.setVal(average);
        //增加当前线程持有节点(即感染者)的感染轮数
        GossipService.setRound(GossipService.getRound() + 1);
        if (!toNode.isInfected()){
            //若被感染者未被感染，则为其启动一个线程
            //将当前线程的轮数保存到被感染者的round
            toNode.setRound(GossipService.getRound());
            //将被感染者的被感染状态设置为真
            toNode.setInfected(true);
            exec.execute(new Gossiper(toNode, nodes, exec, error_limit));
        }
        return true;
    }
}
