/**
 * Producer.java 2:46:36 PM Feb 14, 2012
 *
 * Copyright(c) 2000-2012 HC360.COM, All Rights Reserved.
 */
package org.cc.demo.jms;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;


import org.apache.log4j.Logger;
import org.cc.demo.log.Log;
import org.fusesource.stomp.jms.StompJmsConnectionFactory;


/**
 * 
 * �Ѽ�����ܵ���Ϣ���͵�apollo
 * 
 * @author dixingxing
 * @date Feb 14, 2012
 */
public class PerformanceLogProducer {
	private final static Logger logger = Logger
			.getLogger(PerformanceLogProducer.class);

	private static Connection getConn() throws JMSException {
		StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
		factory.setBrokerURI("tcp://localhost:61613");

		Connection connection = factory.createConnection("admin", "password");
		return connection;
	}

	/**
	 * ��¼��־
	 * 
	 */
	public static void log(Log log) {
		try {
			Connection connection = getConn();
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			Queue queue = session.createQueue("performanceLog");
			MessageProducer producer = session.createProducer(queue);

			String m = log.toString();
			Message msg = session.createTextMessage(m);
			producer.send(msg);
			logger.debug("�Ѿ�������Ϣ��" + m);
			producer.close();
			session.close();
			connection.close();
		} catch (Exception e) {
			logger.error("��¼��־ʧ��", e);
		}
	}

	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 10; i++) {
			Thread.sleep(1000 * 1L);
			// log(log, 10000L);
		}
	}
}
