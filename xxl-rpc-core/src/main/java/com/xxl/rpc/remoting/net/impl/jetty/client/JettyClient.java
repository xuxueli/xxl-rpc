package com.xxl.rpc.remoting.net.impl.jetty.client;

import com.xxl.rpc.remoting.net.Client;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.http.HttpMethod;

import java.util.concurrent.TimeUnit;

//import com.xxl.rpc.registry.ZkServiceDiscovery;

/**
 * jetty client
 *
 * @author xuxueli 2015-11-24 22:25:15
 */
public class JettyClient extends Client {

	@Override
	public XxlRpcResponse send(String address, XxlRpcRequest xxlRpcRequest) throws Exception {

		// reqURL
		if (!address.toLowerCase().startsWith("http")) {
			address = "http://" + address + "/";	// IP:PORT, need parse to url
		}

		// serialize xxlRpcRequest
		byte[] requestBytes = xxlRpcReferenceBean.getSerializer().serialize(xxlRpcRequest);

		// remote invoke
		byte[] responseBytes = postRequest(address, requestBytes, xxlRpcReferenceBean.getTimeout());
		if (responseBytes == null || responseBytes.length==0) {
			XxlRpcResponse xxlRpcResponse = new XxlRpcResponse();
			xxlRpcResponse.setErrorMsg("Network xxlRpcRequest fail, XxlRpcResponse byte[] is null");
			return xxlRpcResponse;
		}

		// deserialize response
		return (XxlRpcResponse) xxlRpcReferenceBean.getSerializer().deserialize(responseBytes, XxlRpcResponse.class);
		
	}


	/**
	 * post request
	 */
	public static byte[] postRequest(String reqURL, byte[] data, long timeout) throws Exception {
		byte[] responseBytes = null;

		// httpclient
		HttpClient httpClient = new HttpClient();
		httpClient.setFollowRedirects(false);	// Configure HttpClient, for example:
		httpClient.start();						// Start HttpClient

		// request
		Request request = httpClient.newRequest(reqURL);
		request.method(HttpMethod.POST);
		request.timeout(timeout, TimeUnit.MILLISECONDS);
		request.content(new BytesContentProvider(data));

		// invoke
		ContentResponse response = request.send();

		// result
		responseBytes = response.getContent();
		return responseBytes;
	}


}
