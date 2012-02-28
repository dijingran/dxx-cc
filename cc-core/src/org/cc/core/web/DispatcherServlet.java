/**
 * DispatcherServlet.java 2:34:04 PM Feb 7, 2012
 *
 * Copyright(c) 2000-2012 HC360.COM, All Rights Reserved.
 */
package org.cc.core.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.cc.core.web.context.WebContext;


/**
 * 
 * 
 * @author dixingxing
 * @date Feb 7, 2012
 */
@SuppressWarnings("serial")
public class DispatcherServlet extends HttpServlet {
	private final static Logger logger = Logger
			.getLogger(DispatcherServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		processRequest(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		processRequest(req, resp);
	}

	protected void beforeProcess() {

	}

	protected void afterProcess(WebMethod webMethod, long start) {

	}

	protected void exceptionOccured(WebMethod webMethod, long start, Throwable e) {

	}

	protected final void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		beforeProcess();
		long start = System.currentTimeMillis();
		WebMethod webMethod = null;
		try {
			logger.debug("(开始)处理请求: " + request.getServletPath());
			webMethod = WebContext.getHandler(request);
			if (webMethod == null) {
				logger.debug("(结束)没有找到控制器!");
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			Object[] params = Binder.bind(request, response, webMethod);

			String s = (String) webMethod.method.invoke(webMethod.handler,
					params);
			response.setContentType("text/html;charset=UTF-8");
			// 直接返回文本
			if (webMethod.isResponseBody) {
				response.getWriter().write(s);
				logger.debug("(结束)直接返回文本");
				afterProcess(webMethod, start);
				return;
			}
			Binder.bind2Request(request, params);

			logger.debug("(结束)返回视图为：" + s);
			request.getRequestDispatcher("/WEB-INF/views/" + s).forward(
					request, response);
			afterProcess(webMethod, start);
		} catch (Exception e) {
			exceptionOccured(webMethod, start, e);
			logger.error(e);
		}

		// ////////////

	}

}