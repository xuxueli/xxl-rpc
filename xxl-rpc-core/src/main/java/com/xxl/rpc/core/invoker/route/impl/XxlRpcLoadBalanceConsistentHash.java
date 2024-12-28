package com.xxl.rpc.core.invoker.route.impl;

import com.xxl.rpc.core.invoker.route.XxlRpcLoadBalance;
import com.xxl.rpc.core.register.entity.RegisterInstance;
import com.xxl.rpc.core.util.XxlRpcException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * consustent hash
 *
 * 单个JOB对应的每个执行器，使用频率最低的优先被选举
 *      a(*)、LFU(Least Frequently Used)：最不经常使用，频率/次数
 *      b、LRU(Least Recently Used)：最近最久未使用，时间
 *
 * @author xuxueli 2018-12-04
 */
public class XxlRpcLoadBalanceConsistentHash extends XxlRpcLoadBalance {

    private int VIRTUAL_NODE_NUM = 100;

    /**
     * get hash code on 2^32 ring (md5散列的方式计算hash值)
     * @param key
     * @return
     */
    private long hash(String key) {

        // md5 byte
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new XxlRpcException("MD5 not supported", e);
        }
        md5.reset();
        byte[] keyBytes = null;
        try {
            keyBytes = key.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new XxlRpcException("Unknown string :" + key, e);
        }

        md5.update(keyBytes);
        byte[] digest = md5.digest();

        // hash code, Truncate to 32-bits
        long hashCode = ((long) (digest[3] & 0xFF) << 24)
                | ((long) (digest[2] & 0xFF) << 16)
                | ((long) (digest[1] & 0xFF) << 8)
                | (digest[0] & 0xFF);

        long truncateHashCode = hashCode & 0xffffffffL;
        return truncateHashCode;
    }

    public RegisterInstance doRoute(String serviceKey, TreeSet<RegisterInstance> instanceTreeSet) {

        // ------A1------A2-------A3------
        // -----------J1------------------
        TreeMap<Long, RegisterInstance> addressRing = new TreeMap<>();
        for (RegisterInstance instance: instanceTreeSet) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                long addressHash = hash("SHARD-" + instance.getUniqueKey() + "-NODE-" + i);
                addressRing.put(addressHash, instance);
            }
        }

        long serviceKeyHash = hash(serviceKey);
        SortedMap<Long, RegisterInstance> lastRing = addressRing.tailMap(serviceKeyHash);
        if (!lastRing.isEmpty()) {
            return lastRing.get(lastRing.firstKey());
        }
        return addressRing.firstEntry().getValue();
    }

    @Override
    public RegisterInstance route(String serviceKey, TreeSet<RegisterInstance> instanceTreeSet) {
        // hash（serviceKey维度，固定hash指定节点）
        return doRoute(serviceKey, instanceTreeSet);
    }

}
