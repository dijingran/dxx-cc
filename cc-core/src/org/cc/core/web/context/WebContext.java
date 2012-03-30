/**
 * WebContext.java 12:04:14 PM Feb 7, 2012
 *
 * Copyright(c) 2000-2012 HC360.COM, All Rights Reserved.
 */
package org.cc.core.web.context;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.cc.core.web.WebMethod;


/**
 * 把所有 {@link WebMethod} 保存在 {@link #MAPPINGS} 中
 * 
 * @author dixingxing
 * @date Feb 7, 2012
 */
public final class WebContext {
	public static final Logger LOGGER = Logger.getLogger(WebContext.class);

	// 
	public static final List<WebMethod> MAPPINGS = new ArrayList<WebMethod>();

	private WebContext(){}
	
	/**
	 * 添加映射方法
	 * 
	 * @param o
	 */
	public static synchronized void addMapping(WebMethod o) {
		MAPPINGS.add(o);
	}

	/**
	 * 根据映射获取对应的webMethod
	 * 
	 * 
	 * @param request
	 * @return
	 */
	public static WebMethod getHandler(HttpServletRequest request) {
		for (WebMethod m : MAPPINGS) {
			if (m.match(request)) {
				LOGGER.debug("匹配到handler ：" + m);
				return m;
			}
		}
		return null;
	}

}