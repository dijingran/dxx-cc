package org.cc.db.dao;

import static java.lang.String.format;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.cc.core.common.ReflectUtils;
import org.cc.db.DbException;
import org.cc.db.jdbc.JdbcConfig;
import org.cc.db.jdbc.JdbcHelper;

public class Dao<T> implements IDao<T> {
	private static final Logger LOG = Logger.getLogger(Dao.class);
	
	@SuppressWarnings("unused")
	private List<T> mapList(ResultSet rs, Class<T> clazz) {
		Field[] fields = ReflectUtils.getVariableFields(clazz);
		List<Map<String, Object>> maps = JdbcHelper.getMap(rs, fields);

		List<T> list = new ArrayList<T>();
		if (maps != null && maps.size() > 0) {
			for (Map<String, Object> map : maps) {
				list.add(new Mapper<T>(clazz).mapObject(map, fields));
			}
		}
		return list;
	}

	private T mapOne(ResultSet rs, Class<T> clazz) {
		Field[] fields = ReflectUtils.getVariableFields(clazz);
		List<Map<String, Object>> maps = JdbcHelper.getMap(rs, fields);
		if (maps == null || maps.size() == 0) {
			return null;
		}
		return new Mapper<T>(clazz).mapObject(maps.get(0), fields);
	}

	/**
	 * 获取在子类中定义的泛型
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Class<T> poClass() {
		// 使用cglib代理，获取实际类型为getSuperclass()
		return (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public T query(Long id) {
		final Connection cn = getConnection();
		SqlHolder holder = SqlBuilder.buildQueryById(poClass(), id);
		LOG.debug(holder);
		PreparedStatement pstmt = JdbcHelper.getPstmt(cn, holder.getSql());
		JdbcHelper.setParams(pstmt, holder.getParams());
		ResultSet rs = JdbcHelper.executeQuery(pstmt);
		T po = mapOne(rs, poClass());
		JdbcHelper.close(rs, pstmt);
//		JdbcHelper.close(cn);
		return po;
	}

	public Long insert(T po) {
		SqlHolder holder = SqlBuilder.buildInsert(po);
		update(holder);

		// 返回id
		Long id = queryLong(SqlBuilder.buildGetInsertId(poClass()));
		ReflectUtils.set(po, "id", id);
		return id;
	}

	public List<T> queryList(String sql, Object... params) {
		final Connection cn = getConnection();
		SqlHolder holder = new SqlHolder(sql, params);
		LOG.debug(holder);
		PreparedStatement pstmt = JdbcHelper.getPstmt(cn, holder.getSql());
		JdbcHelper.setParams(pstmt, holder.getParams());
		ResultSet rs = JdbcHelper.executeQuery(pstmt);
		List<T> list = mapList(rs, poClass());
		JdbcHelper.close(rs, pstmt);
//		JdbcHelper.close(cn);
		return list;
	}

	public T query(String sql, Object... params) {
		List<T> list = queryList(sql, params);
		if (list.size() == 0) {
			return null;
		} else if (list.size() > 1) {
			throw new DbException(format("期望返回1条数据，实际返回%d条记录", list.size()));
		}
		return list.get(0);
	}

	public Long queryLong(String sql, Object... params) {
		final Connection cn = getConnection();
		SqlHolder holder = new SqlHolder(sql, params);
		LOG.debug(holder);
		PreparedStatement pstmt = JdbcHelper.getPstmt(cn, holder.getSql());
		JdbcHelper.setParams(pstmt, holder.getParams());
		ResultSet rs = JdbcHelper.executeQuery(pstmt);
		Long result = JdbcHelper.getNumber(rs, Long.class);
//		JdbcHelper.close(cn);
		return result;
	}

	public void update(T po) {
		SqlHolder holder = SqlBuilder.buildUpdate(po);
		update(holder);
	}

	/**
	 * 执行update
	 * 
	 * @param holder
	 */
	public void update(SqlHolder holder) {
		final Connection cn = getConnection();
//		JdbcHelper.setAutoCommit(cn, false);
		LOG.debug(holder);
		PreparedStatement pstmt = JdbcHelper.getPstmt(cn, holder.getSql());
		JdbcHelper.setParams(pstmt, holder.getParams());
		JdbcHelper.executeUpdate(pstmt);
		JdbcHelper.close(null, pstmt);
//		JdbcHelper.close(cn);
	}

	public void execute(String sql, Object... params) {
		SqlHolder holder = new SqlHolder();
		update(holder);
	}

	/**
	 * 获取连接
	 * 
	 * @return
	 */
	public Connection getConnection() {
		return JdbcConfig.getConnectionProvider().getConn();
	}
}