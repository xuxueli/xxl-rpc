package com.xxl.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxl.controller.interceptor.PermessionLimit;
import com.xxl.core.model.XxlRpcInfo;
import com.xxl.core.result.ReturnT;
import com.xxl.core.util.ZkServiceUtils;

/**
 * Base 
 * @author xuxueli 2016-3-19 13:56:28
 */
@Controller
@RequestMapping("/rpc")
public class RpcController {
	
	@RequestMapping("")
	@PermessionLimit
	public String index(){
		return "rpc/index";
	}
	
	@RequestMapping("/pageList")
	@ResponseBody
	@PermessionLimit
	public Map<String, Object> pageList(Model model, String iface){
		model.addAttribute("iface", iface);
		
		ZkServiceUtils.getInstance().freshServiceAddress();
		List<XxlRpcInfo> list = new ArrayList<XxlRpcInfo>();
		if (MapUtils.isNotEmpty(ZkServiceUtils.getInstance().serviceAddress)) {
			for (Entry<String, Set<String>> item: ZkServiceUtils.getInstance().serviceAddress.entrySet()) {
				XxlRpcInfo info = new XxlRpcInfo();
				info.setIface(item.getKey());
				if (CollectionUtils.isNotEmpty(item.getValue())) {
					info.setProviders(new ArrayList<String>(item.getValue()));
				}
				list.add(info);
			}
		}
		// package result
		Map<String, Object> maps = new HashMap<String, Object>();
	    maps.put("recordsTotal", list.size());		// 总记录数
	    maps.put("recordsFiltered", list.size());	// 过滤后的总记录数
	    maps.put("data", list);
		return maps;
	}
	
	@RequestMapping("/remove")
	@ResponseBody
	@PermessionLimit
	public ReturnT<String> remove(String iface){
		boolean ret = ZkServiceUtils.getInstance().removeIface(iface);
		return ret?ReturnT.SUCCESS:new ReturnT<String>(500, "移除失败");
	}
}
