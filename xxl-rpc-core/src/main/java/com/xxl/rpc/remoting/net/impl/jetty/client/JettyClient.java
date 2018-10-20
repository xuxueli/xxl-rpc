package com.xxl.rpc.remoting.net.impl.jetty.client;

import com.xxl.rpc.remoting.invoker.XxlRpcInvokerFactory;
import com.xxl.rpc.remoting.net.Client;
import com.xxl.rpc.remoting.net.params.XxlRpcFutureResponse;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;

import java.util.concurrent.TimeUnit;

//import com.xxl.rpc.registry.ZkServiceDiscovery;

/**
 * jetty client
 *
 * @author xuxueli 2015-11-24 22:25:15
 */
public class JettyClient extends Client {

	@Override
	public void asyncSend(String address, XxlRpcRequest xxlRpcRequest) throws Exception {
		// reqURL
		if (!address.startsWith("http")) {
			address = "http://" + address + "/";	// IP:PORT, need parse to url
		}

		// do invoke
		postRequestAsync(address, xxlRpcRequest);
	}

    /**
     * post request (async)
     *
     * @param reqURL
     * @return
     * @throws Exception
     */
    private void postRequestAsync(String reqURL, XxlRpcRequest xxlRpcRequest) throws Exception {

		// serialize request
		byte[] requestBytes = xxlRpcReferenceBean.getSerializer().serialize(xxlRpcRequest);


        // httpclient
        HttpClient httpClient = new HttpClient();
        httpClient.setFollowRedirects(false);	// Configure HttpClient, for example:
        httpClient.start();						// Start HttpClient

        // request
        Request request = httpClient.newRequest(reqURL);
        request.method(HttpMethod.POST);
        request.timeout(xxlRpcReferenceBean.getTimeout(), TimeUnit.MILLISECONDS);
        request.content(new BytesContentProvider(requestBytes));

        // invoke
        request.send(new BufferingResponseListener() {
			@Override
			public void onComplete(Result result) {

				// valid status
				if (result.isFailed()) {
					throw new RuntimeException("xxl-rpc remoting request fail, " + result.getResponseFailure().getMessage());
				}

				// valid HttpStatus
				if (result.getResponse().getStatus() != HttpStatus.OK_200) {
					throw new RuntimeException("xxl-rpc remoting request fail, http HttpStatus["+ result.getResponse().getStatus() +"] invalid.");
				}

				// valid response bytes
				byte[] responseBytes = getContent();
				if (responseBytes == null || responseBytes.length==0) {
					throw new RuntimeException("xxl-rpc remoting request fail, response bytes is empty.");
				}

				// deserialize response
				XxlRpcResponse xxlRpcResponse = (XxlRpcResponse) xxlRpcReferenceBean.getSerializer().deserialize(responseBytes, XxlRpcResponse.class);

				// wait response
				XxlRpcFutureResponse futureResponse = XxlRpcInvokerFactory.getInvokerFuture(xxlRpcResponse.getRequestId());
				futureResponse.setXxlRpcResponse(xxlRpcResponse);

			}
		});
    }

	/*@Override
	public XxlRpcResponse send(String address, XxlRpcRequest xxlRpcRequest) throws Exception {

		// reqURL
		if (!address.toLowerCase().startsWith("http")) {
			address = "http://" + address + "/";	// IP:PORT, need parse to url
		}

		// serialize request
		byte[] requestBytes = xxlRpcReferenceBean.getSerializer().serialize(xxlRpcRequest);

		// remote invoke
		byte[] responseBytes = postRequest(address, requestBytes, xxlRpcReferenceBean.getTimeout());

		// deserialize response
		return (XxlRpcResponse) xxlRpcReferenceBean.getSerializer().deserialize(responseBytes, XxlRpcResponse.class);

	}*/

	/**
	 * post request
	 */
	/*private static byte[] postRequest(String reqURL, byte[] data, long timeout) throws Exception {

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
		if (response.getStatus() != HttpStatus.OK_200) {
			throw new RuntimeException("xxl-rpc remoting request fail, http HttpStatus["+ response.getStatus() +"] invalid.");
		}

		// result
		byte[] responseBytes = response.getContent();
		if (responseBytes == null || responseBytes.length==0) {
			throw new RuntimeException("xxl-rpc remoting request fail, response bytes is empty.");
		}

		return responseBytes;
	}*/


}
