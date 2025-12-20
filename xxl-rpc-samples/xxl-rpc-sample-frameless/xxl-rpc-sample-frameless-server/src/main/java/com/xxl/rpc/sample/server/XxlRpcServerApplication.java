package com.xxl.rpc.sample.server;

import com.xxl.rpc.sample.server.conf.FramelessXxlRpcConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2018-10-21 20:48:40
 */
public class XxlRpcServerApplication {
    private static final Logger logger = LoggerFactory.getLogger(XxlRpcServerApplication.class);

    public static void main(String[] args) throws Exception {

        // start
        FramelessXxlRpcConfig.getInstance().start();
        logger.info(">>>>>>>>>>> xxl-mq frameless started.");

        while (!Thread.currentThread().isInterrupted()) {
            TimeUnit.HOURS.sleep(1);
        }

        // stop
        FramelessXxlRpcConfig.getInstance().stop();
    }

}
