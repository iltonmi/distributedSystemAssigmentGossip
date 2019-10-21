package com.company;


import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Gossip任务
 * @author lin weili
 */

public class GossipJob {
    /**
     * 创建具有指定线程数量的固定线程池，并运行Gossip任务。
     * @param THREAD_NUM 线程数量
     * @param ERROR_LIMIT 相对误差
     * @param initialVal 初始值
     * @param nodeNum 节点数量
     * @param k K值
     */
    public void runGossip(int THREAD_NUM, double ERROR_LIMIT, double initialVal, int nodeNum, double k){
        //创建线程池
        ThreadPoolExecutor exec = new ThreadPoolExecutor(THREAD_NUM, THREAD_NUM, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());

        Node[] nodes = new Node[nodeNum];
        for (int i = 0; i < nodes.length; ++i){
            //为数组填充初始值为0、未感染的并且未隔离的节点
            Node node = new Node(i, false, 0, false, k);
            nodes[i] = node;
        }

        //选取数组的第一个节点为启动节点
        Node start = nodes[0];
        //设置启动节点的初始值为指定initialVal
        start.setVal(initialVal);
        //设置启动节点的infected状态为true
        start.setInfected(true);
        //以启动节点为参数创建一个Runnable，并交由线程池执行
        exec.execute(new Gossiper(start, nodes, exec, ERROR_LIMIT));


        long completedTaskCount = 0;
        while (true){
            if (exec.getActiveCount() == 0){
                try{
                    //JDK的getActiveCount方法只能获取大约的活跃线程数量
                    //所以当getActiveCount返回O时，线程池的所以任务不一定执行完成
                    //因此强制主线程休眠300毫秒
                    Thread.sleep(300);
                    //getCompletedTaskCount判断休眠前后，线程池已完成的任务数量是否发生变化
                    if (completedTaskCount == exec.getCompletedTaskCount()){
                        //无变化，则说明线程池已经执行完所有任务，退出循环
                        break;
                    }else {
                        //有变化，说明getActiveCount返回O时，线程池任务并未全部完成
                        //更新线程池已完成任务数量
                        completedTaskCount = exec.getCompletedTaskCount();
                    }
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
        //退出循环说明线程池中所有任务均已完成，关闭线程池
        exec.shutdown();

        //求各节点绝对误差之和
        double totalAbsoluteError = 0;
        double average = initialVal / nodeNum;
        for (int i = 0; i < nodeNum; ++i){
            totalAbsoluteError += Math.abs(nodes[i].getVal() - average);
        }
        //求最大轮数
        int maxRound = Integer.MIN_VALUE;
        for (int i = 0; i < nodeNum; ++i){
            maxRound = nodes[i].getRound() > maxRound ?
                    nodes[i].getRound() : maxRound;
        }
        //打印runGossip的结果
        System.out.println(nodeNum + "," + k + "," + totalAbsoluteError / nodeNum + "," + maxRound + "," + exec.getCompletedTaskCount());
    }
}
