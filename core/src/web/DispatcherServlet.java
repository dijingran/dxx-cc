/**
 * DispatcherServlet.java 2:34:04 PM Feb 7, 2012
 *
 * Copyright(c) 2000-2012 HC360.COM, All Rights Reserved.
 */
package web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import web.context.WebContext;

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

	@SuppressWarnings("unchecked")
	protected final void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		logger.debug("(��ʼ)��������: " + request.getServletPath());
		long start = System.currentTimeMillis();
		WebMethod webMethod = WebContext.getHandler(request);
		if (webMethod == null) {
			logger.debug("(����)û���ҵ�������!");
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			logTime(start);
			return;
		}
		try {

			Object[] params = Binder.bind(request, response, webMethod);

			String s = (String) webMethod.method.invoke(webMethod.handler,
					params);

			// ֱ�ӷ����ı�
			if (webMethod.isResponseBody) {
				response.setContentType("text/html;charset=UTF-8");
				PrintWriter pw = response.getWriter();
				pw.write(s);
				pw.close();
				logTime(start);
				logger.debug("(����)ֱ�ӷ����ı�");
				return;
			}
			Binder.bind2Request(request, params);

			logger.debug("(����)������ͼΪ��" + s);
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/WEB-INF/views/" + s);
			dispatcher.forward(request, response);
			logTime(start);
		} catch (Exception e) {
			logger.error("���÷���" + webMethod.method.getName() + "����", e);
		}
	}

	private void logTime(long start) {
		logger.debug("��ʱ:" + (System.currentTimeMillis() - start) + "����");
	}
}
