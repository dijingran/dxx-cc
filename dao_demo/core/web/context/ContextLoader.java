/**
 * ContextLoader.java 11:55:24 AM Feb 7, 2012
 *
 * Copyright(c) 2000-2012 HC360.COM, All Rights Reserved.
 */
package web.context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import web.WebException;
import web.annotation.Controller;
import web.annotation.RequestMapping;
import web.annotation.ResponseBody;

/**
 * ��ʼ��web����
 * 
 * @author dixingxing
 * @date Feb 7, 2012
 */
public class ContextLoader implements ServletContextListener {
	private final static Logger logger = Logger.getLogger(ContextLoader.class);

	public void contextDestroyed(ServletContextEvent arg0) {
		logger.debug("context destroyed !");

	}

	public void contextInitialized(ServletContextEvent arg0) {
		logger.debug("context initialized !");
		// ����һ��ɨ�账�������ų��ڲ��� ɨ�������������
		ClassPathScanUtils handler = new ClassPathScanUtils(true, true, null);

		Set<Class<?>> calssList = handler.getPackageAllClasses("web", true);
		for (Class<?> clazz : calssList) {
			if (clazz.isAnnotationPresent(Controller.class)) {
				addWebMethods(clazz);
			}
		}
	}

	/**
	 * ����ӳ���ϵ��WebContext
	 * 
	 * @param clazz
	 * @return
	 */
	private List<WebMethod> addWebMethods(Class<?> clazz) {
		List<WebMethod> list = new ArrayList<WebMethod>();
		Method[] methods = clazz.getDeclaredMethods();

		String[] urlPathMain = null;
		if (clazz.isAnnotationPresent(RequestMapping.class)) {
			RequestMapping rm = clazz.getAnnotation(RequestMapping.class);
			urlPathMain = rm.value();
		}
		Object handler = WebContext.handlers.get(clazz.getSimpleName());
		if (handler == null) {
			try {
				handler = clazz.newInstance();
				WebContext.handlers.put(clazz.getSimpleName(), handler);
			} catch (Exception e) {
				logger.error(clazz.getName() + "ʵ����ʱ����", e);
				throw new WebException("ʵ����ʱ����", e);
			}
		}
		for (Method m : methods) {
			if (m.isAnnotationPresent(RequestMapping.class)) {
				RequestMapping rm = m.getAnnotation(RequestMapping.class);
				WebMethod webMethod = new WebMethod();
				webMethod.handler = handler;
				webMethod.method = m;
				webMethod.urlPathMain = urlPathMain;
				webMethod.urlPath = rm.value();
				webMethod.requestMethod = rm.method();
				webMethod.isResponseBody = m
						.isAnnotationPresent(ResponseBody.class);
				logger.debug(String.format("װ��%s.%s", clazz.getName(), m
						.getName()));
				logger.debug(webMethod);
				WebContext.mappings.add(webMethod);
			}
		}

		return list;
	}

}
