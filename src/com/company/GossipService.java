package com.company;

/**
 * Gossip服务类
 * @author lin weili
 */

public class GossipService {

    /**
     * 利用ThreadLocal为每个线程保留独立副本的特性，保存线程持有节点的感染轮数
     */
    private static ThreadLocal<Integer> round = ThreadLocal.withInitial(() -> 0);

    public static void setRound(Integer val){
        round.set(val);
    }

    public static Integer getRound(){
        return round.get();
    }
}
