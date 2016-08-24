package com.xxl.rpc.netcom.common.server;

import com.xxl.rpc.serialize.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * i server
 * @author xuxueli 2015-11-24 20:59:49
 */
public abstract class IServer {
	private static final Logger logger = LoggerFactory.getLogger(IServer.class);

	public abstract void start(final int port, final Serializer serializer, final Map<String, Object> serviceMap, final boolean zookeeper_switch) throws Exception;

}
