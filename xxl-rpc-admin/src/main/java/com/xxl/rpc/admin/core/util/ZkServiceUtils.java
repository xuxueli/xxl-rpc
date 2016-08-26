package com.xxl.rpc.admin.core.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * zookeeper service discovery
 * @author xuxueli 2015-10-29 17:29:32
 */
public class ZkServiceUtils {
    private static final Logger logger = LoggerFactory.getLogger(ZkServiceUtils.class);

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
	/**
	 * 	/xxl-rpc/iface1/address1
	 * 	/xxl-rpc/iface1/address2
	 * 	/xxl-rpc/iface1/address3
	 * 	/xxl-rpc/iface2/address1
	 */
	public static volatile ConcurrentMap<String, Set<String>> serviceAddress = new ConcurrentHashMap<String, Set<String>>();

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

	public static boolean removeIface(String iface){
    	if (iface==null || iface.trim().length()==0) {
			return false;
		}
    	String znodePath = Environment.ZK_SERVICES_PATH.concat("/").concat(iface);

		try {
			Stat stat = getInstance().exists(znodePath, true);
			if (stat != null) {
				List<String> childPath = getInstance().getChildren(znodePath, true);
				if (CollectionUtils.isNotEmpty(childPath)) {
					for (String path : childPath) {
						getInstance().delete(znodePath+"/"+path, -1);
					}
				}
				getInstance().delete(znodePath, stat.getVersion());
			} else {
				logger.info(">>>>>>>>>> znodeKey[{}] not found.", znodePath);
			}

		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		return true;
    }

}