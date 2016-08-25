package com.xxl.rpc.registry;

import com.xxl.rpc.util.Environment;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * zookeeper service discovery
 * @author xuxueli 2015-10-29 17:29:32
 */
public class ZkServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ZkServiceDiscovery.class);

	// ------------------------------ zookeeper client ------------------------------
	private static ZooKeeper zooKeeper;
	private static ReentrantLock INSTANCE_INIT_LOCK = new ReentrantLock(true);

	private static ZooKeeper getInstance(){
		if (zooKeeper==null) {
			try {
				if (INSTANCE_INIT_LOCK.tryLock(2, TimeUnit.SECONDS)) {
					/*final CountDownLatch countDownLatch = new CountDownLatch(1);
					countDownLatch.countDown();
					countDownLatch.await();*/
					zooKeeper = new ZooKeeper(Environment.ZK_ADDRESS, 30000, new Watcher() {
						@Override
						public void process(WatchedEvent event) {
							// session expire, close old and create new
							if (event.getState() == Event.KeeperState.Expired) {
								try {
									zooKeeper.close();
								} catch (InterruptedException e) {
									logger.error("", e);
								}
								zooKeeper = null;
							}
							// add One-time trigger, ZooKeeper的Watcher是一次性的，用过了需要再注册
							try {
								String znodePath = event.getPath();
								if (znodePath != null) {
									zooKeeper.exists(znodePath, true);
								}
							} catch (KeeperException e) {
								logger.error("", e);
							} catch (InterruptedException e) {
								logger.error("", e);
							}

							// refresh service address
							if (event.getType() == Event.EventType.NodeChildrenChanged || event.getState() == Event.KeeperState.SyncConnected) {
								freshServiceAddress();
							}

						}
					});

					logger.info(">>>>>>>>> xxl-rpc zookeeper connnect success.");
				}
			} catch (InterruptedException e) {
				logger.error("", e);
			} catch (IOException e) {
				logger.error("", e);
			}
		}
		if (zooKeeper == null) {
			throw new NullPointerException(">>>>>>>>>>> xxl-rpc, zookeeper connect fail.");
		}
		return zooKeeper;
	}


	// ------------------------------ discover service ------------------------------
	private static Executor executor = Executors.newCachedThreadPool();
	static {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					freshServiceAddress();
					try {
						TimeUnit.SECONDS.sleep(10L);
					} catch (InterruptedException e) {
						logger.error("", e);
					}
				}
			}
		});
	}

	/**
	 * 	/xxl-rpc/iface1/address1
	 * 	/xxl-rpc/iface1/address2
	 * 	/xxl-rpc/iface1/address3
	 * 	/xxl-rpc/iface2/address1
     */
    private static volatile ConcurrentMap<String, Set<String>> serviceAddress = new ConcurrentHashMap<String, Set<String>>();

    public static void freshServiceAddress(){
		ConcurrentMap<String, Set<String>> tempMap = new ConcurrentHashMap<String, Set<String>>();
		try {
    		// iface list
			List<String> interfaceNameList = getInstance().getChildren(Environment.ZK_SERVICES_PATH, true);

			if (interfaceNameList!=null && interfaceNameList.size()>0) {
				for (String interfaceName : interfaceNameList) {

					// address list
					String ifacePath = Environment.ZK_SERVICES_PATH.concat("/").concat(interfaceName);
					List<String> addressList = getInstance().getChildren(ifacePath, true);

					if (addressList!=null && addressList.size() > 0) {
						Set<String> addressSet = new HashSet<String>(); 
						for (String address : addressList) {

							// data from address
							String addressPath = ifacePath.concat("/").concat(address);
							byte[] bytes = getInstance().getData(addressPath, false, null);
							addressSet.add(new String(bytes));
						}
						tempMap.put(interfaceName, addressSet);
					}
				}
				serviceAddress = tempMap;
				logger.info(">>>>>>>>>>> xxl-rpc fresh serviceAddress success: {}", serviceAddress);
			}
	        
    	} catch (KeeperException e) {
			logger.error("", e);
		} catch (InterruptedException e) {
			logger.error("", e);
		}
    }
    
    public static String discover(String interfaceName) {
    	logger.debug(">>>>>>>>>>>> discover:{}", serviceAddress);
		freshServiceAddress();
    	Set<String> addressSet = serviceAddress.get(interfaceName);
    	if (addressSet==null || addressSet.size()==0) {
			return null;
		}
    	String address;
    	List<String> addressArr = new ArrayList<String>(addressSet);
        int size = addressSet.toArray().length;
        if (size == 1) {
            address = addressArr.get(0);
        } else {
        	address = addressArr.get(new Random().nextInt(size));
        }
        return address;
    }
    
}