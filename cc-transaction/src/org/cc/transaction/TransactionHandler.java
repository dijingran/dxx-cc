/**
 * TransactionHandler.java 4:36:52 PM Apr 12, 2012
 *
 * Copyright(c) 2000-2012 HC360.COM, All Rights Reserved.
 */
package org.cc.transaction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

/**
 * 
 * 
 * @author dixingxing
 * @date Apr 12, 2012
 */
public class TransactionHandler implements InvocationHandler {
	private static final Logger LOG = Logger.getLogger(TransactionHandler.class);

	private Object target;

	public TransactionHandler(Object target) {
		this.target = target;
	}

	public void before(Method method, Object[] args) {
	}

	public void after(Method method, Object[] args) {
		Transactional tran = getDefinition(method);
		
		Method implMethod = getImplMethod(method);

		if (tran == null || tran.readonly()) {
			LOG.debug("当前方法没有定义事务或定义为只读，开始回滚事务");
			TransactionProvider.rollback(implMethod);
		} else {
			LOG.debug("当前方法定义了事务，开始提交事务");
			TransactionProvider.commit(implMethod);
		}
	}

	/**
	 * <p>获取权限定义{@link Transactional} </p>
	 * 
	 * @param m
	 * @return
	 */
	public Transactional getDefinition(Method m) {
		// m是接口中定义的方法
		if (m.isAnnotationPresent(Transactional.class)) {
			return m.getAnnotation(Transactional.class);
		}
		
		
		// 如果接口中没定义annotation，那么看实现类里面有没有定义annotation
		Method method = null;
		try {
			method = target.getClass().getMethod(m.getName(), m.getParameterTypes());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
		
		if (method.isAnnotationPresent(Transactional.class)) {
			return method.getAnnotation(Transactional.class);
		}
		return target.getClass().getAnnotation(Transactional.class);
	}
	
	
	private Method getImplMethod (Method m) {
		Method method = null;
		try {
			method = target.getClass().getMethod(m.getName(), m.getParameterTypes());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
		return method;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		before(method, args);
		Object result = null;
		try {
			result = method.invoke(target, args);
		} catch (Exception e) {
			e.printStackTrace();
			TransactionProvider.rollback(method);
		}
		after(method, args);
		return result;
	}
}