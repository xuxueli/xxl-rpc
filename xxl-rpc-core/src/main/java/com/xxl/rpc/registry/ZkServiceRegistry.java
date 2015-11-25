package com.xxl.rpc.registry;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.rpc.util.Environment;

/**
 * zookeeper service registry
 * @author xuxueli 2015-10-29 14:43:46
 */
public class ZkServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ZkServiceRegistry.class);
    
    public static ZkServiceRegistry serviceRegistry = new ZkServiceRegistry();
    
    private ZooKeeper zooKeeper = null;
    public ZkServiceRegistry() {
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
            logger.info(">>>>>>>>> xxl-mq provider connnect zookeeper success.");
            // init base
            Stat stat = this.zooKeeper.exists(Constant.ZK_SERVICES_REGISTRY, true);
			if (stat == null) {
				zooKeeper.create(Constant.ZK_SERVICES_REGISTRY, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
        } catch (IOException e) {
        	logger.error("", e);
        } catch (InterruptedException e) {
        	logger.error("", e);
        } catch (KeeperException e) {
        	logger.error("", e);
		}
	}
    
    /**
     * register service
     */
    public void registerServices(int port, Set<String> serviceList) {
    	// valid
    	if (port < 1 || (serviceList==null || serviceList.size()==0)) {
    		return;
    	}
    	String ip = null;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		if (ip == null) {
			return;
		}
		// register
		String serverAddress = ip + ":" + port;
		for (String interfaceName : serviceList) {
			try {
				// init servicePath prefix : servicePath : xxl-rpc/interfaceName/serverAddress(ip01:port9999)
				String ifacePath = Constant.ZK_SERVICES_REGISTRY.concat("/").concat(interfaceName);
				String addressPath = Constant.ZK_SERVICES_REGISTRY.concat("/").concat(interfaceName).concat("/").concat(serverAddress);
				// parent path must be persistent
				if (this.zooKeeper.exists(ifacePath, true) == null) {
					zooKeeper.create(ifacePath, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				}
				// register service path
				String path = zooKeeper.create(addressPath, serverAddress.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
	            logger.info(">>>>>>>>>>> xxl-rpc register service on zookeeper success, "
	            		+ "interfaceName:{}, serverAddress:{}, path:{}", interfaceName, serverAddress, path);
	            
	        } catch (KeeperException e) {
	        	logger.error("", e);
	        } catch (InterruptedException e) {
	        	logger.error("", e);
	        }
		}
    }
    
}