package com.xxl.core.util;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.collections.CollectionUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * zookeeper service discovery
 * @author xuxueli 2015-10-29 17:29:32
 */
public class ZkServiceUtils implements Watcher {
    private static final Logger logger = LoggerFactory.getLogger(ZkServiceUtils.class);
   
    private static ZkServiceUtils instance = new ZkServiceUtils();
    public static ZkServiceUtils getInstance() {
		return instance;
	}

    private ZooKeeper zooKeeper = null;
    public ZkServiceUtils() {
    	final CountDownLatch latch = new CountDownLatch(1);
        try {
        	zooKeeper = new ZooKeeper(Environment.getZkserver(), 5000, new Watcher() {
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });
        	latch.await();
        	logger.info(">>>>>>>>> xxl-mq consumer connnect zookeeper success.");
        	// init base
            Stat stat = this.zooKeeper.exists(Constant.ZK_SERVICES_REGISTRY, true);
			if (stat == null) {
				zooKeeper.create(Constant.ZK_SERVICES_REGISTRY, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			// fresh service address
            freshServiceAddress();
        } catch (IOException e) {
        	logger.error("", e);
        } catch (InterruptedException e) {
        	logger.error("", e);
        } catch (KeeperException e) {
        	logger.error("", e);
		}
    }
    
    @Override
	public void process(WatchedEvent event) {
    	try {
			if (event.getType() == Event.EventType.NodeChildrenChanged) {
				freshServiceAddress();
	    	}
			String znodePath = event.getPath();
			if (znodePath != null) {
				this.zooKeeper.exists(znodePath, true);	// add One-time trigger, ZooKeeper的Watcher是一次性的，用过了需要再注册
			}
		} catch (KeeperException e) {
			logger.error("", e);
		} catch (InterruptedException e) {
			logger.error("", e);
		}
	}
    
    // servicePath : xxl-rpc/interfaceName/netComType(netty)/serverAddress(ip:port)
    // interfaceName:[address01, address02]
    public volatile ConcurrentMap<String, Set<String>> serviceAddress = new ConcurrentHashMap<String, Set<String>>();
    public void freshServiceAddress(){
    	try {
    		ConcurrentMap<String, Set<String>> tempMap = new ConcurrentHashMap<String, Set<String>>();
    		// interfaceName from base
			List<String> interfaceNameList = zooKeeper.getChildren(Constant.ZK_SERVICES_REGISTRY, this);
			if (interfaceNameList!=null && interfaceNameList.size()>0) {
				for (String interfaceName : interfaceNameList) {
					// addressList from iface
					String ifacePath = Constant.ZK_SERVICES_REGISTRY.concat("/").concat(interfaceName);
					List<String> addressList = zooKeeper.getChildren(ifacePath, this);
					if (addressList!=null && addressList.size() > 0) {
						Set<String> addressSet = new HashSet<String>(); 
						for (String address : addressList) {
							// data from address
							String addressPath = Constant.ZK_SERVICES_REGISTRY.concat("/").concat(interfaceName).concat("/").concat(address);
							byte[] bytes = zooKeeper.getData(addressPath, false, null);
							addressSet.add(new String(bytes));
						}
						tempMap.put(interfaceName, addressSet);
					}
				}
				serviceAddress = tempMap;
				logger.info(">>>>>>>>>>> xxl-rpc fresh serviceAddress: {}", serviceAddress);
			}
    	} catch (KeeperException e) {
			logger.error("", e);
		} catch (InterruptedException e) {
			logger.error("", e);
		}
    }
    
    public boolean removeIface(String iface){
    	if (iface==null || iface.trim().length()==0) {
			return false;
		}
    	String ifacePath = Constant.ZK_SERVICES_REGISTRY.concat("/").concat(iface);
    	this.delete(ifacePath);
    	return true;
    }
    
    /**
	 * delete exist node
	 * @param znodeKey
	 */
	public void delete(String znodePath){
		try {
			Stat stat = this.zooKeeper.exists(znodePath, true);
			if (stat != null) {
				List<String> childPath = this.zooKeeper.getChildren(znodePath, this);
				if (CollectionUtils.isNotEmpty(childPath)) {
					for (String path : childPath) {
						this.zooKeeper.delete(znodePath+"/"+path, -1);
					}
				}
				this.zooKeeper.delete(znodePath, stat.getVersion());
			} else {
				logger.info(">>>>>>>>>> znodeKey[{}] not found.", znodePath);
			}
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
    
}