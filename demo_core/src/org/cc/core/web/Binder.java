/**
 * Binder.java 5:23:54 PM Feb 7, 2012
 *
 * Copyright(c) 2000-2012 HC360.COM, All Rights Reserved.
 */
package org.cc.core.web;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.cc.core.common.ReflectUtils;
import org.cc.core.web.annotation.PathVar;



/**
 * ���ݰ���
 * 
 * @author dixingxing
 * @date Feb 7, 2012
 */
public class Binder {
	private final static Logger logger = Logger.getLogger(Binder.class);

	/**
	 * 
	 * controller�з��������İ�
	 * 
	 * @param request
	 * @param response
	 * @param webMethod
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static Object[] bind(HttpServletRequest request,
			HttpServletResponse response, WebMethod webMethod)
			throws InstantiationException, IllegalAccessException {
		logger.debug("��controller�з�������");
		// �����Ĳ���
		Class<?>[] paramClasses = webMethod.method.getParameterTypes();

		Annotation[][] annotations = webMethod.method.getParameterAnnotations();

		Object[] paramValues = new Object[paramClasses.length];

		if (paramClasses.length == 0) {
			return paramValues;
		}

		for (int i = 0; i < paramClasses.length; i++) {
			Class<?> cls = paramClasses[i];
			// request
			if (cls.isAssignableFrom(HttpServletRequest.class)) {
				paramValues[i] = request;
			}
			// response
			else if (cls.isAssignableFrom(HttpServletResponse.class)) {
				paramValues[i] = response;
			}
			// model
			else if (cls.isAssignableFrom(Model.class)) {
				paramValues[i] = new Model();
			}
			// @PathVariable
			else if (annotations[i] != null && isPathVar(annotations[i])) {
				// ��uri�н���������path variable
				String[] variables = webMethod.getPathVars(request
						.getServletPath());
				PathVar pv = (PathVar) getPathVarAnnotation(annotations[i]);
				if (pv.value() >= variables.length) {
					logger.warn("PathVar.value()ֵΪ :" + pv.value()
							+ ",Ӧ��С��ƥ�䵽�Ĳ������� : " + variables.length);
					break;
				}
				String v = variables[pv.value()];
				Object value = convertValue(cls, v);
				if (value != null) {
					logger.debug("����pathVariable�ɹ���ֵΪ:" + value);
					paramValues[i] = value;
				} else {
					paramValues[i] = cls.newInstance();
				}
			}
			// pojo
			else {
				paramValues[i] = bind(request, cls.newInstance());
			}
		}
		return paramValues;
	}

	/**
	 * ������󶨵�request��,����ͼ(jsp)ʹ��
	 * 
	 * @param req
	 * @param objects
	 */
	public static void bind2Request(HttpServletRequest req, Object[] objects) {
		logger.debug("������󶨵�request��,����ͼ(jsp)ʹ��");
		for (Object o : objects) {
			Class<?> clazz = o.getClass();
			if (clazz.isAssignableFrom(Model.class)) {
				bindModel2Request(req, (Model) o);
			} else {
				bindObject2Request(req, o);
			}
		}
	}

	/**
	 * ͨ�������request�еĲ�������obj��Ӧ������
	 * 
	 * @param request
	 * @param obj
	 * @throws IllegalAccessException
	 */
	private static Object bind(HttpServletRequest request, Object obj) {
		for (Field f : ReflectUtils.getVariableFields(obj.getClass())) {

			String param = request.getParameter(f.getName());

			if (param == null) {
				continue;
			}
			Class<?> clazz = f.getType();
			Object value = convertValue(clazz, param);
			if (value == null) {
				continue;
			}

			ReflectUtils.set(obj, f, value);
		}
		return obj;
	}

	/**
	 * 
	 * ��model�еĶ���ŵ�request�У�<br/>����ǵ���model.addAttribute(Object)�����ŵ�model�еģ���ô�Ѵ�object����������ֵ�ŵ�request��
	 * 
	 * @see Model#addAttribute(Object)
	 * @param req
	 * @param m
	 */
	private static void bindModel2Request(HttpServletRequest req, Model m) {
		for (String key : m.keySet()) {
			Object o = m.get(key);
			// �󶨶����������
			if (key.startsWith(Model.DEFAULT_KEY)) {
				for (Field f : ReflectUtils.getVariableFields(o.getClass())) {
					req.setAttribute(f.getName(), ReflectUtils.get(o, f));
				}
			}
			// ֱ�Ӱ󶨶���
			else {
				req.setAttribute(key, o);
			}
		}
	}

	/**
	 * controller�з��������İ�<br/> ͨ�������obj��Ӧ�����Էŵ�request��
	 * 
	 * @param req
	 * @param o
	 */
	private static void bindObject2Request(HttpServletRequest req, Object o) {
		// request �� response��������Ҫ����
		if (o.getClass().isAssignableFrom(HttpServletRequest.class)
				|| o.getClass().isAssignableFrom(HttpServletResponse.class)) {
			return;
		}
		for (Field f : ReflectUtils.getVariableFields(o.getClass())) {
			req.setAttribute(f.getName(), ReflectUtils.get(o, f));
		}
	}

	/**
	 * ����date
	 * 
	 * 
	 * @param s
	 * @return
	 */
	private static Date parseDate(String s) {
		Date d = null;
		try {
			d = new SimpleDateFormat("yyyy-MM-dd").parse(s);
		} catch (ParseException e) {
		}

		if (d != null) {
			return d;
		}

		try {
			d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);
		} catch (ParseException e1) {
			throw new RuntimeException("����date����", e1);
		}
		return d;
	}

	/**
	 * ��ȡPathVarע��
	 * 
	 * @param annotations
	 * @return
	 */
	private static Annotation getPathVarAnnotation(Annotation[] annotations) {
		for (Annotation a : annotations) {
			if (PathVar.class.equals(a.annotationType())) {
				return a;
			}
		}
		return null;
	}

	/**
	 * �Ǽ���PathVarע��Ĳ���
	 * 
	 * @param annotations
	 * @return
	 */
	private static boolean isPathVar(Annotation[] annotations) {
		for (Annotation a : annotations) {
			if (PathVar.class.equals(a.annotationType())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * ��String ת������Ӧ����
	 * 
	 * @param cls
	 * @param value
	 * @param v
	 * @return
	 */
	private static Object convertValue(Class<?> cls, String v) {
		Object value = null;
		try {
			if (cls.equals(Long.TYPE) || cls.equals(Long.class)) {
				value = Long.valueOf(v);

			} else if (cls.equals(Double.TYPE) || cls.equals(Double.class)) {
				value = Double.valueOf(v);

			} else if (cls.equals(Integer.TYPE) || cls.equals(Integer.class)) {
				value = Integer.valueOf(v);

			} else if (cls.equals(String.class)) {
				value = v;

			} else if (cls.equals(Date.class)) {
				value = parseDate(v);
			} else {
				logger.error("���ܰ󶨵�PathVariable ����");
			}
		} catch (Exception e) {
			logger.error("����ת������", e);
			throw new WebException("����ת������", e);
		}
		return value;
	}

}
