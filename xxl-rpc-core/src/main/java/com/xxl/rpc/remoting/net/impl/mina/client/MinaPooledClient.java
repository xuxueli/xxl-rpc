package com.xxl.rpc.remoting.net.impl.mina.client;

import com.xxl.rpc.remoting.net.impl.mina.codec.MinaDecoder;
import com.xxl.rpc.remoting.net.impl.mina.codec.MinaEncoder;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import com.xxl.rpc.remoting.net.pool.ClientPooled;
import com.xxl.rpc.serialize.Serializer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.transport.socket.DefaultSocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * mina pooled client
 *
 * @author xuxueli
 */
public class MinaPooledClient extends ClientPooled {


	private NioSocketConnector connector;
	private IoSession ioSession;


	@Override
	public void init(String host, int port, final Serializer serializer) {

		connector = new NioSocketConnector();
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ProtocolCodecFactory() {
			@Override
			public ProtocolEncoder getEncoder(IoSession session) throws Exception {
				return new MinaEncoder(XxlRpcRequest.class, serializer);
			}
			@Override
			public ProtocolDecoder getDecoder(IoSession session) throws Exception {
				return new MinaDecoder(XxlRpcResponse.class, serializer);
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


	@Override
	public boolean isValidate() {
		if (this.ioSession != null && this.connector != null) {
			return this.connector.isActive() && this.ioSession.isConnected();
		}
		return false;
	}


	@Override
	public void close() {
		if (this.ioSession != null) {
			if (this.ioSession.isConnected()) {
				ioSession.getCloseFuture().awaitUninterruptibly();
			}
			this.ioSession.closeNow();
			this.ioSession = null;
		}
		if (this.connector != null) {
			this.connector.dispose();
			this.connector = null;
		}
		logger.debug(">>>>>>>>>>>> xxl-rpc mina client close.");
	}

	@Override
	public void send(XxlRpcRequest xxlRpcRequest) {
		this.ioSession.write(xxlRpcRequest);
    }

}
