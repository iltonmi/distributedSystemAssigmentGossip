package com.company;



import java.math.RoundingMode;
import java.text.NumberFormat;

public class Main {

    /**
     * 根据Java并发编程的艺术：
     * 因为这是cpu密集型任务，所以线程数量应该和我的4核cpu处理器数量+1，即4+1=5
     */
    public final static int THREAD_NUM = 5;

    /**
     * 误差限，10^-6
     */
    public final static double ERROR_LIMIT = 0.000001;

    private static NumberFormat nf;

    static {
        nf = NumberFormat.getNumberInstance();
        // 保留两位小数
        nf.setMaximumFractionDigits(2);
        nf.setRoundingMode(RoundingMode.DOWN);
    }

    public static void main(String[] args) {

        //第一部分：节点数量为变量，保持K不变
        int startNodeNum = 200;
        int maxNodeNum = 1001;
        int nodeNumStep = 200;
        double startK = 1.1;
        double maxK = 2.1;
        double kStep = 0.2;
        runGossipWithVariableK(startNodeNum, maxNodeNum, nodeNumStep, startK, maxK, kStep);

        //第二部分：K为变量，保持节点数量不变
        double startK2 = 1.1;
        double maxK2 = 2.1;
        double kStep2 = 0.2;
        int startNodeNum2 = 200;
        int maxNodeNum2 = 1001;
        int nodeNumStep2 = 200;
        runGossipWithVariableNodeNumber(startNodeNum2, maxNodeNum2, nodeNumStep2, startK2, maxK2, kStep2);

    }

    /**
     * 创建GossipJob，展示以K为值变量，在几个不同节点数的情况下Gossip的传播情况。
     *
     * @param startNodeNum 起始节点数
     * @param maxNodeNum 最大节点数
     * @param nodeNumStep 节点数步长
     * @param startK 起始K值
     * @param maxK 最大K值
     * @param kStep K值步长
     */
    private static void runGossipWithVariableK(int startNodeNum, int maxNodeNum, int nodeNumStep,
                                  double startK, double maxK, double kStep){
        System.out.println("节点数量,K,误差,收敛轮数,感染节点数");
        //建立gossip批处理任务
        GossipJob job = new GossipJob();
        for (double k = startK; k < maxK; k += kStep){
            for (int n = startNodeNum; n < maxNodeNum; n += nodeNumStep){
                //运行gossip任务
                job.runGossip(THREAD_NUM, ERROR_LIMIT, n, n, Double.valueOf(nf.format(k)));
            }
        }
    }

    /**
     * 创建GossipJob，展示以节点数量为变量，在几个不同K值的情况下Gossip的传播情况。
     *
     * @param startNodeNum 起始节点数
     * @param maxNodeNum 最大节点数
     * @param nodeNumStep 节点数步长
     * @param startK 起始K值
     * @param maxK 最大K值
     * @param kStep K值步长
     */
    private static void runGossipWithVariableNodeNumber(int startNodeNum, int maxNodeNum, int nodeNumStep,
                                               double startK, double maxK, double kStep){
        System.out.println("节点数量,K,误差,收敛轮数,感染节点数");
        //建立gossip批处理任务
        GossipJob job = new GossipJob();
        for (int n = startNodeNum; n < maxNodeNum; n += nodeNumStep){
            for (double k = startK; k < maxK; k += kStep){
                //运行gossip任务
                job.runGossip(THREAD_NUM, ERROR_LIMIT, n, n, Double.valueOf(nf.format(k)));
            }
        }
    }

}
