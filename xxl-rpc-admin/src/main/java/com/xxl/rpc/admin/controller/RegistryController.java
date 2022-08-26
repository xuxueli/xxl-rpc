package com.xxl.rpc.admin.controller;

import com.xxl.rpc.admin.core.model.XxlRpcRegistry;
import com.xxl.rpc.admin.core.result.ReturnT;
import com.xxl.rpc.admin.service.IXxlRpcRegistryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
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
                                        String env,
                                        String key){
        return xxlRpcRegistryService.pageList(start, length, env, key);
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




}
