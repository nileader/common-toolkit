package common.toolkit.java.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class BaseInterceptorOfSpringMVC extends HandlerInterceptorAdapter {

	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		StringBuffer requestURL = request.getRequestURL();
		String basePath = requestURL.substring(0, requestURL.indexOf(request.getContextPath()))+request.getContextPath();
		request.setAttribute("baseUrl", basePath);
		request.setAttribute("basePath", basePath);
		setFlagIsFirerox(request);
		return super.preHandle(request, response, handler);
	}

	private void setFlagIsFirerox(HttpServletRequest request) {
		String s = request.getHeader("user-agent");
		boolean isFirefox = false;
		if (null != s && s.toLowerCase().indexOf("firefox") > 0) {
			isFirefox = true;
		}
		request.setAttribute("isFirefox", isFirefox);
	}

}
