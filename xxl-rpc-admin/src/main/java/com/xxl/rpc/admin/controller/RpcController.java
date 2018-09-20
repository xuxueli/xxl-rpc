package com.xxl.rpc.admin.controller;

import com.xxl.rpc.admin.controller.annotation.PermessionLimit;
import com.xxl.rpc.admin.core.model.XxlRpcInfo;
import com.xxl.rpc.admin.core.result.ReturnT;
import com.xxl.rpc.admin.core.util.ZkServiceUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.Map.Entry;

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
		
		ZkServiceUtils.freshServiceAddress();
		List<XxlRpcInfo> list = new ArrayList<XxlRpcInfo>();
		if (MapUtils.isNotEmpty(ZkServiceUtils.serviceAddress)) {
			for (Entry<String, Set<String>> item: ZkServiceUtils.serviceAddress.entrySet()) {
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
		boolean ret = ZkServiceUtils.removeIface(iface);
		return ret?ReturnT.SUCCESS:new ReturnT<String>(500, "移除失败");
	}
}
