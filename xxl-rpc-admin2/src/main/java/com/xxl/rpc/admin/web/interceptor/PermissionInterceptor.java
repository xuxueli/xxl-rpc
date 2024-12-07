package com.xxl.rpc.admin.web.interceptor;

import com.xxl.rpc.admin.annotation.Permission;
import com.xxl.rpc.admin.constant.enums.RoleEnum;
import com.xxl.rpc.admin.model.dto.LoginUserDTO;
import com.xxl.rpc.admin.model.dto.ResourceDTO;
import com.xxl.rpc.admin.util.I18nUtil;
import com.xxl.rpc.admin.service.impl.LoginService;
import com.xxl.tool.exception.BizException;
import com.xxl.tool.freemarker.FreemarkerTool;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 权限拦截
 *
 * @author xuxueli 2015-12-12 18:09:04
 */
@Component
public class PermissionInterceptor implements AsyncHandlerInterceptor {

	@Resource
	private LoginService loginService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		// handler method
		if (!(handler instanceof HandlerMethod)) {
			return true;	// proceed with the next interceptor
		}
		HandlerMethod method = (HandlerMethod)handler;

		// parse permission config
		Permission permission = method.getMethodAnnotation(Permission.class);
		if (permission == null) {
			throw new BizException("权限拦截，请求路径权限未设置");
		}
		if (!permission.login()) {
			return true;	// not need login ,not valid permission, pass
		}

		// valid login
		LoginUserDTO loginUser = loginService.checkLogin(request, response);
		if (loginUser == null) {
			response.setStatus(302);
			response.setHeader("location", request.getContextPath() + "/toLogin");
			return false;
		}
		request.setAttribute(LoginService.LOGIN_IDENTITY_KEY, loginUser);

		// valid role
		if (RoleEnum.matchByValue(permission.role()) == RoleEnum.ADMIN) {
			// admin user
			if (RoleEnum.matchByValue(loginUser.getRole()) == RoleEnum.ADMIN) {
				return true;
			} else {
				throw new BizException(I18nUtil.getString("system_permission_limit"));
			}
		} else {
			// normal user, pass
			return true;
		}

	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

		if (modelAndView != null) {
			// i18n, static method
			modelAndView.addObject("I18nUtil", FreemarkerTool.generateStaticModel(I18nUtil.class.getName()));

			// default menu
			List<ResourceDTO> resourceDTOList = new ArrayList<>();
			resourceDTOList.addAll(Arrays.asList(
					new ResourceDTO(1, 0, "首页",0, "", "/index", "fa fa-home", 1, 0),
					new ResourceDTO(2, 0, "应用管理",0, "", "/application", " fa-cloud", 2, 0),
					new ResourceDTO(3, 0, "注册节点管理",0, "", "/instance", " fa-cubes", 3, 0),
					new ResourceDTO(6, 0, "帮助中心",0, "", "/help", "fa-book", 6, 0)
			));
			if (loginService.isAdmin(request)) {
				resourceDTOList.addAll(Arrays.asList(
						new ResourceDTO(4, 0, "环境管理",0, "", "/environment", "fa-cog", 4, 0),
						new ResourceDTO(5, 0, "用户管理",0, "", "/user", "fa-users", 5, 0)
				));
			}
			resourceDTOList.sort(new Comparator<ResourceDTO>() {
				@Override
				public int compare(ResourceDTO o1, ResourceDTO o2) {
					return o1.getOrder() - o2.getOrder();
				}
			});


			modelAndView.addObject("resourceList", resourceDTOList);

		}

	}

}
