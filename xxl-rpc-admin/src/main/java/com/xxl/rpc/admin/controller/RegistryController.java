package com.xxl.rpc.admin.controller;

import com.xxl.rpc.admin.controller.annotation.PermessionLimit;
import com.xxl.rpc.admin.core.model.XxlRpcRegistry;
import com.xxl.rpc.admin.core.result.ReturnT;
import com.xxl.rpc.admin.service.IXxlRpcRegistryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author xuxueli 2018-11-21
 */
@Controller
@RequestMapping("/registry")
public class RegistryController {

    @Resource
    private IXxlRpcRegistryService xxlRpcRegistryService;


    @RequestMapping("")
    public String index(Model model){
        return "registry/registry.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        String biz,
                                        String env,
                                        String key){
        return xxlRpcRegistryService.pageList(start, length, biz, env, key);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ReturnT<String> delete(int id){
        return xxlRpcRegistryService.delete(id);
    }

    @RequestMapping("/update")
    @ResponseBody
    public ReturnT<String> update(XxlRpcRegistry xxlRpcRegistry){
        return xxlRpcRegistryService.update(xxlRpcRegistry);
    }

    @RequestMapping("/add")
    @ResponseBody
    public ReturnT<String> add(XxlRpcRegistry xxlRpcRegistry){
        return xxlRpcRegistryService.add(xxlRpcRegistry);
    }


    // ---------------------- remote registry ----------------------

    @RequestMapping("/registry")
    @ResponseBody
    @PermessionLimit(limit=false)
    public ReturnT<String> registry(String biz, String env, @RequestParam(name = "keys", required = false) List<String> keys, String value){
        return xxlRpcRegistryService.registry(biz, env, keys, value);
    }

    @RequestMapping("/remove")
    @ResponseBody
    @PermessionLimit(limit=false)
    public ReturnT<String> remove(String biz, String env, @RequestParam(name = "keys", required = false) List<String> keys, String value){
        return xxlRpcRegistryService.remove(biz, env, keys, value);
    }

    @RequestMapping("/discovery")
    @ResponseBody
    @PermessionLimit(limit=false)
    public ReturnT<Map<String, List<String>>> discovery(String biz, String env, @RequestParam(name = "keys", required = false) List<String> keys) {
        return xxlRpcRegistryService.discovery(biz, env, keys);
    }

    @RequestMapping("/monitor")
    @ResponseBody
    @PermessionLimit(limit=false)
    public DeferredResult monitor(String biz, String env, @RequestParam(name = "keys", required = false) List<String> keys) {
        return xxlRpcRegistryService.monitor(biz, env, keys);
    }

}
