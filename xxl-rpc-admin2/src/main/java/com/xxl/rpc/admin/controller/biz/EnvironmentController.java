package com.xxl.rpc.admin.controller.biz;

import com.xxl.rpc.admin.annotation.Permission;
import com.xxl.rpc.admin.model.entity.Environment;
import com.xxl.rpc.admin.service.EnvironmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import javax.annotation.Resource;

import com.xxl.tool.response.Response;
import com.xxl.tool.response.PageModel;
import com.xxl.tool.response.ResponseBuilder;

import static com.xxl.rpc.admin.controller.biz.UserController.ADMIN_ROLE;

/**
* Environment Controller
*
* Created by xuxueli on '2024-12-07 15:40:35'.
*/
@Controller
@RequestMapping("/environment")
public class EnvironmentController {

    @Resource
    private EnvironmentService environmentService;

    /**
    * 页面
    */
    @RequestMapping
    @Permission(role = ADMIN_ROLE)
    public String index(Model model) {
        return "biz/environment";
    }

    /**
    * 分页查询
    */
    @RequestMapping("/pageList")
    @ResponseBody
    @Permission(role = ADMIN_ROLE)
    public Response<PageModel<Environment>> pageList(@RequestParam(required = true, defaultValue = "0") int offset,
                                                     @RequestParam(required = true, defaultValue = "10") int pagesize,
                                                     @RequestParam(required = false) String env,
                                                     @RequestParam(required = false) String name) {
        PageModel<Environment> pageModel = environmentService.pageList(offset, pagesize, env, name);
        return new ResponseBuilder<PageModel<Environment>>().success(pageModel).build();
    }

    /**
    * Load查询
    */
    @RequestMapping("/load")
    @ResponseBody
    @Permission(role = ADMIN_ROLE)
    public Response<Environment> load(int id){
        return environmentService.load(id);
    }

    /**
    * 新增
    */
    @RequestMapping("/insert")
    @ResponseBody
    @Permission(role = ADMIN_ROLE)
    public Response<String> insert(Environment xxlRpcEnvironment){
        return environmentService.insert(xxlRpcEnvironment);
    }

    /**
    * 删除
    */
    @RequestMapping("/delete")
    @ResponseBody
    @Permission(role = ADMIN_ROLE)
    public Response<String> delete(@RequestParam("ids[]") List<Integer> ids){
        return environmentService.delete(ids);
    }

    /**
    * 更新
    */
    @RequestMapping("/update")
    @ResponseBody
    @Permission(role = ADMIN_ROLE)
    public Response<String> update(Environment xxlRpcEnvironment){
        return environmentService.update(xxlRpcEnvironment);
    }

}
