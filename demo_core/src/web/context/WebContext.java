/**
 * WebContext.java 12:04:14 PM Feb 7, 2012
 *
 * Copyright(c) 2000-2012 HC360.COM, All Rights Reserved.
 */
package web.context;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import web.WebMethod;

/**
 * 
 * 
 * @author dixingxing
 * @date Feb 7, 2012
 */
public class WebContext {
	private final static Logger logger = Logger.getLogger(WebContext.class);

	// 
	public final static List<WebMethod> mappings = new ArrayList<WebMethod>();

	/**
	 * ���ӳ�䷽��
	 * 
	 * @param o
	 */
	public static synchronized void addMapping(WebMethod o) {
		mappings.add(o);
	}

	/**
	 * ����ӳ���ȡ��Ӧ��webMethod
	 * 
	 * 
	 * @param request
	 * @return
	 */
	public static WebMethod getHandler(HttpServletRequest request) {
		for (WebMethod m : mappings) {
			if (m.match(request)) {
				logger.debug("ƥ�䵽handler ��" + m);
				return m;
			}
		}
		return null;
	}

}
