package com.xxl.rpc.netcom.mina.client;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.transport.socket.DefaultSocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.rpc.netcom.common.codec.RpcRequest;
import com.xxl.rpc.netcom.common.codec.RpcResponse;
import com.xxl.rpc.netcom.mina.codec.MinaDecoder;
import com.xxl.rpc.netcom.mina.codec.MinaEncoder;
import com.xxl.rpc.serialize.Serializer;

/**
 * connetion proxy
 * @author xuxueli
 */
public class MinaClientPoolProxy {
	private static transient Logger logger = LoggerFactory.getLogger(MinaClientPoolProxy.class);
	
	private NioSocketConnector connector;
	private IoSession ioSession;
	public void createProxy(String host, int port, final Serializer serializer) throws InterruptedException {
		
		connector = new NioSocketConnector();
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ProtocolCodecFactory() {
			@Override
			public ProtocolEncoder getEncoder(IoSession session) throws Exception {
				return new MinaEncoder(RpcRequest.class, serializer);
			}
			@Override
			public ProtocolDecoder getDecoder(IoSession session) throws Exception {
				return new MinaDecoder(RpcResponse.class, serializer);
			}
		}));
		connector.setHandler(new MinaClientHandler());
		connector.setConnectTimeoutMillis(5000);
		
		DefaultSocketSessionConfig sessionConfiguration = (DefaultSocketSessionConfig) connector.getSessionConfig();
		sessionConfiguration.setReadBufferSize(1024);
		sessionConfiguration.setSendBufferSize(512);
		sessionConfiguration.setReuseAddress(true);
		sessionConfiguration.setTcpNoDelay(true);
		sessionConfiguration.setKeepAlive(true);
		sessionConfiguration.setSoLinger(-1);
		sessionConfiguration.setWriteTimeout(5);
		
		ConnectFuture future = connector.connect(new InetSocketAddress(host, port));
		future.awaitUninterruptibly(5, TimeUnit.SECONDS);
		
		if (!future.isConnected()) {
			logger.error(">>>>>>>>>>>> xxl-rpc mina client proxy, connect to server fail at host:{}, port:{}", host, port);
			connector.dispose();
			connector = null;
			return;
		}
		this.ioSession = future.getSession();
		logger.debug(">>>>>>>>>>>> xxl-rpc mina client proxy, connect to server success at host:{}, port:{}", host, port);
	}
	
	public IoSession getIoSession() {
		return this.ioSession;
	}
	
	public boolean isValidate() {
		if (this.ioSession != null && this.connector != null) {
			return this.connector.isActive() && this.ioSession.isConnected();
		}
		return false;
	}
	
	public void close() {
		if (this.ioSession != null) {
			if (this.ioSession.isConnected()) {
				ioSession.getCloseFuture().awaitUninterruptibly();
			}
			this.ioSession.close(true);
			this.ioSession = null;
		}
		if (this.connector != null) {
			this.connector.dispose();
			this.connector = null;
		}
		logger.debug(">>>>>>>>>>>> xxl-rpc mina client proxy close.");
	}
	
	public void send(RpcRequest request) throws Exception {
		this.ioSession.write(request);
    }
}
