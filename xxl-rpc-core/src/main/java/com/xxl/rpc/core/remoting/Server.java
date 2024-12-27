package com.xxl.rpc.core.remoting;

import com.xxl.rpc.core.factory.XxlRpcFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * server
 *
 * @author xuxueli 2015-11-24 20:59:49
 */
public abstract class Server {
	protected static final Logger logger = LoggerFactory.getLogger(Server.class);


	// ---------------------- callback ----------------------

	/**
	 * started callback
	 */
	private Callable<Void> startedCallback;

	/**
	 * stoped callback
	 */
	private Callable<Void> stopedCallback;

	public void setStartedCallback(Callable<Void> startedCallback) {
		this.startedCallback = startedCallback;
	}

	public void setStopedCallback(Callable<Void> stopedCallback) {
		this.stopedCallback = stopedCallback;
	}


	// ---------------------- start/stop ----------------------

	/**
	 * start server
	 *
	 * @param factory
	 * @throws Exception
	 */
	public abstract void start(final XxlRpcFactory factory) throws Exception;

	/**
	 * stop server
	 *
	 * @throws Exception
	 */
	public abstract void stop() throws Exception;


	/**
	 * callback when started
	 */
	public void onStarted() {
		if (startedCallback != null) {
			try {
				startedCallback.call();
			} catch (Exception e) {
				logger.error(">>>>>>>>>>> xxl-rpc, server startedCallback error.", e);
			}
		}
	}

	/**
	 * callback when stoped
	 */
	public void onStoped() {
		if (stopedCallback != null) {
			try {
				stopedCallback.call();
			} catch (Exception e) {
				logger.error(">>>>>>>>>>> xxl-rpc, server stopedCallback error.", e);
			}
		}
	}

}