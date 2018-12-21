package com.xxl.rpc.sample.client.controller;

import com.jfinal.core.Controller;
import com.xxl.rpc.sample.api.dto.UserDTO;
import com.xxl.rpc.sample.client.config.XxlRpcClient;

/**
 * @author xuxueli 2018-12-21
 */
public class IndexController extends Controller {

	public void index(){

		try {
			UserDTO userDTO = XxlRpcClient.instance.getDemoService().sayHi(getPara("name"));
			renderJson(userDTO);
		} catch (Exception e) {
			e.printStackTrace();
			renderText(e.getMessage());
		}

	}

}
