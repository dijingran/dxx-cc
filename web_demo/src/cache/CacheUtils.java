/**
 * CacheUtils.java 9:49:57 AM Feb 15, 2012
 *
 * Copyright(c) 2000-2012 HC360.COM, All Rights Reserved.
 */
package cache;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.TextCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.transcoders.SerializingTranscoder;

import org.apache.log4j.Logger;

import po.Ad;

import com.google.code.yanf4j.core.impl.StandardSocketOption;

/**
 * xmemecached
 * 
 * @author dixingxing
 * @date Feb 15, 2012
 */
public class CacheUtils {
	private static Logger logger = Logger.getLogger(CacheUtils.class);
	private static final String CACHE_ERROR = "��������ʧ�ܣ�";
	private static MemcachedClient cache;

	// ��ʱʱ������Ϊ10��
	private static long TIME_OUT = 5000L;

	static {
		logger.debug("---------��ʼ��memcachedClient ��ʼ---------");
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(
				getAddresses());
		builder.setSocketOption(StandardSocketOption.SO_RCVBUF, 128 * 1024); // ���ý��ջ�������Ĭ��64K
		builder.setSocketOption(StandardSocketOption.SO_SNDBUF, 32 * 1024); // ���÷��ͻ�������Ĭ��Ϊ16K
		builder.setSocketOption(StandardSocketOption.TCP_NODELAY, false); // ����nagle�㷨�������������Ĭ�Ϲر�
		builder.setConnectionPoolSize(2); //
		builder.setCommandFactory(new TextCommandFactory());
		builder.setSessionLocator(new KetamaMemcachedSessionLocator());
		builder.setTranscoder(new SerializingTranscoder());

		builder.getConfiguration().setSessionIdleTimeout(10000L); // Ĭ��������ӳ���5��û���κ�IO������������Ϊ���в������������
		builder.getConfiguration().setStatisticsServer(true);

		try {
			cache = builder.build();
		} catch (Exception e) {
			logger.error("��ʼ��xmemcached client ʧ��", e);
		}
		cache.setOpTimeout(TIME_OUT);

		cache.setMergeFactor(50); // Ĭ����150����С��50
		cache.setOptimizeMergeBuffer(false); // �رպϲ�buffer���Ż�
		cache.setEnableHeartBeat(false);

		logger.debug("---------��ʼ��memcachedClient ����---------");

	}

	/**
	 * ��̬���memcached �ڵ�
	 * 
	 * @param host
	 * @param port
	 */
	public static void addServer(String host, int port) {
		try {
			cache.addServer(host, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ĭ�ϵ�memcached ��������ַ
	 * 
	 * @return
	 */
	private static List<InetSocketAddress> getAddresses() {
		List<InetSocketAddress> list = new ArrayList<InetSocketAddress>();
		list.add(new InetSocketAddress("127.0.0.1", 11211));
		return list;
	}

	/**
	 * ���뻺��
	 * 
	 * @param key
	 * @param exp
	 *            ��
	 * @param value
	 *            Ҫʵ��Serializable
	 */
	public static void set(final String key, final int exp, final Object value) {
		if (key == null || value == null) {
			return;
		}
		try {
			cache.set(key, exp, value, TIME_OUT);
			logger.debug("set into memcached " + key);
		} catch (Exception e) {
			logger.error(CACHE_ERROR, e);
		}
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public static <T> T get(final String key) {
		if (key == null) {
			return null;
		}
		T o = null;
		try {
			o = cache.get(key, TIME_OUT);
			logger.debug("get from memcached " + key);
		} catch (Exception e) {
			logger.error(CACHE_ERROR, e);
		}
		return (T) o;
	}

	/**
	 * ɾ��
	 * 
	 * @param key
	 */
	public static void delete(final String key) {
		try {
			cache.delete(key);
		} catch (Exception e) {
			logger.error(CACHE_ERROR, e);
		}
	}

	public static void main(String[] args) throws MemcachedException,
			InterruptedException, TimeoutException {
		for (int i = 0; i < 200; i++) {
			CacheUtils.set("test_" + i, 15, "test_v_" + i);
		}
		for (int i = 0; i < 200; i++) {
			System.out.println(CacheUtils.get("test_" + i));
		}
		Ad ad = new Ad();
		ad.setName("ad_name");
		ad.setId(123L);
		CacheUtils.set("ad_test", 5, ad);
		System.out.println(CacheUtils.get("ad_test"));
	}
}
