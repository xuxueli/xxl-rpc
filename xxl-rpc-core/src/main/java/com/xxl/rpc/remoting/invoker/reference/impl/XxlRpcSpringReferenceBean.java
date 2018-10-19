package com.xxl.rpc.remoting.invoker.reference.impl;

import com.xxl.rpc.remoting.invoker.reference.XxlRpcReferenceBean;
import com.xxl.rpc.remoting.net.NetEnum;
import com.xxl.rpc.remoting.net.params.CallType;
import com.xxl.rpc.serialize.Serializer;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * rpc reference bean, use by spring xml and annotation (for spring)
 *
 * @author xuxueli 2015-10-29 20:18:32
 */
public class XxlRpcSpringReferenceBean extends XxlRpcReferenceBean implements FactoryBean<Object>, InitializingBean {


    // ---------------------- config ----------------------

    private String netType;
    private String serialize;
    private String address;
    private String accessToken;

    private Class<?> iface;
    private String version;

    private long timeout = 5000;	// million
    private String callType;


    // set
    public void setNetType(String netType) {
        this.netType = netType;
    }

    public void setSerialize(String serialize) {
        this.serialize = serialize;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setIface(Class<?> iface) {
        this.iface = iface;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }


    // util
    private void prepareConfig() throws Exception {

        // prepare config
        NetEnum netTypeEnum = NetEnum.autoMatch(netType, null);
        Serializer.SerializeEnum serializeEnum = Serializer.SerializeEnum.match(serialize, null);
        Serializer serializer = serializeEnum!=null?serializeEnum.serializer:null;
        CallType callTypeEnum = CallType.match(callType, null);
        if (timeout < 0) {
            timeout = 200;
        }

        // init config
        super.initConfig(netTypeEnum, serializer, address, accessToken, iface, version, timeout, callTypeEnum);
    }

    // ---------------------- util ----------------------

    @Override
    public void afterPropertiesSet() throws Exception {
        prepareConfig();
    }


    @Override
    public Object getObject() throws Exception {
        return super.getObject();
    }

    @Override
    public Class<?> getObjectType() {
        return super.getObjectType();
    }

    @Override
    public boolean isSingleton() {
        return false;
    }


    /**
     *	public static <T> ClientProxy ClientProxy<T> getFuture(Class<T> type) {
     *		<T> ClientProxy proxy = (<T>) new ClientProxy();
     *		return proxy;
     *	}
     */


}
