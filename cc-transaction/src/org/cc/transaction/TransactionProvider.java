/**
 * Provider.java 3:54:28 PM Apr 12, 2012
 *
 * Copyright(c) 2000-2012 HC360.COM, All Rights Reserved.
 */
package org.cc.transaction;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.cc.core.common.ReflectUtils;
import org.cc.db.transaction.Provider;

/**
 * 
 * 
 * @author dixingxing
 * @date Apr 12, 2012
 */
public class TransactionProvider implements Provider {
	private static final Logger LOG = Logger.getLogger(TransactionProvider.class);

	private static ThreadLocal<Map<Method, Connection>> holder = new ThreadLocal<Map<Method, Connection>>();

	public Connection getConnection() {
		Method m = getTransactionMethod();
		return getMap().get(m);
	}
	
	

	public boolean hasConn(Connection conn) {
		return getMap().containsValue(conn);
	}



	public boolean putConnection(Connection conn) {
		Map<Method, Connection> map = holder.get();
		if (map == null) {
			map = new HashMap<Method, Connection>();
			holder.set(map);
		}

		Method m = getTransactionMethod();
		if(m == null) {
//			LOG.debug(String.format("put connection failed %s ",conn));
			return false;
		}
		LOG.debug(String.format("put connection %s ,method %s",conn,m != null ? m.getName() : ""));
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		// 定义transaction注解
		map.put(m, conn);
		return true;
	}

	public static void rollback(Method m) {
		Connection conn = getMap().get(m);
		if (conn != null) {
			try {
				conn.rollback();
				LOG.debug(String.format("回滚方法%s.%s的事务",m.getDeclaringClass().getName(),m.getName()));
			} catch (SQLException e) {
				throw new RuntimeException("回滚失败", e);
			} finally {
				closeAndRemove(m,conn);
			}
		}
	}

	public static void commit(Method m) {
		Connection conn = getMap().get(m);
		if (conn != null) {
			try {
				conn.commit();
				LOG.debug(String.format("提交方法%s.%s的事务",m.getDeclaringClass().getName(),m.getName()));
			} catch (SQLException e) {
				throw new RuntimeException("提交失败", e);
			} finally {
				closeAndRemove(m,conn);
			}
		}
	}

	/**
	 * 关闭连接并把连接从holder中移除
	 * 
	 * @param m
	 * @param conn
	 */
	private static void closeAndRemove(Method m,Connection conn) {
		try {
			conn.close();
			remove(m);
			LOG.debug("释放数据库连接");
		} catch (SQLException e) {
			throw new RuntimeException("回滚失败", e);
		}
	}
	
	/**
	 * 每个事务完成后要把map中的conn移除，。
	 * 
	 * @param m
	 */
	private static void remove(Method m) {
		getMap().remove(m);
	}

	public static Method getTransactionMethod() {
		Method returnMethod = null;
		StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
		try {
			for (StackTraceElement ste : stacks) {
				// 说明是测试类。
				if(ste.getClassName().indexOf("JUnit") >= 0) {
					break;
				}
				Class<?> clazz = Class.forName(ste.getClassName());
				// class级别
				if (clazz.isAnnotationPresent(Transactional.class)) {
					returnMethod = clazz.getMethod(ste.getMethodName());
				}
				// 方法级别 TODO 如果一个测试类中有同名方法时会有问题
				Method[] methods = ReflectUtils.getVariableMethods(clazz);
				for (Method m : methods) {
					if (!m.getName().equals(ste.getMethodName())) {
						continue;
					}
					if (m.isAnnotationPresent(Transactional.class)) {
						LOG.debug("调用者：" + ste.getMethodName() + "，行号:"
								+ ste.getLineNumber());
						returnMethod = m;
					}
				}

			}
			return returnMethod;
		} catch (Exception e) {
			LOG.error("出现异常，设置数据库事务为只读", e);
			throw new RuntimeException(e);
		}
	}

	private static Map<Method, Connection> getMap() {
		Map<Method, Connection> map = holder.get();
		if (map == null) {
			map = new HashMap<Method, Connection>();
			holder.set(map);
		}
		return map;
	}
	
}
