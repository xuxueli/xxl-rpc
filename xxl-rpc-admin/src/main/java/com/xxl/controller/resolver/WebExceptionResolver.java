package com.xxl.controller.resolver;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.xxl.core.result.ReturnT;

/**
 * 异常解析器
 * @author xuxueli
 */
public class WebExceptionResolver implements HandlerExceptionResolver {
	private static transient Logger logger = LoggerFactory.getLogger(WebExceptionResolver.class);

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		ModelAndView mv = new ModelAndView();
		
		HandlerMethod method = null;
		ResponseBody responseBody = null;
		if (handler instanceof HandlerMethod) {
			method = (HandlerMethod)handler;
			responseBody = method.getMethodAnnotation(ResponseBody.class);
		}
		if (responseBody != null) {
			String result = null;
			try {
				result = new ObjectMapper().writeValueAsString(new ReturnT<String>(500, ex.getMessage()));
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mv.addObject("result", result);
			mv.setViewName("common/common.result");
		} else {
			mv.addObject("exceptionMsg", ex.getMessage());	
			mv.setViewName("common/common.exception");
		}
		
		logger.info("==============异常开始=============");
		logger.info("system catch exception:{}", ex);
		logger.info("==============异常结束=============");
		
		return mv;
	}

	
}
