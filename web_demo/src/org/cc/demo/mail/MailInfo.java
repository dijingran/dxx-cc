package org.cc.demo.mail;

import java.util.Properties;

/**
 * �����ʼ���Ҫʹ�õĻ�����Ϣ
 */
public class MailInfo {
	// �����ʼ��ķ�������IP�Ͷ˿�
	private String mailServerHost = "smtp.yeah.net";
	private String mailServerPort = "25";
	// ��½�ʼ����ͷ��������û���������
	private String userName = "dxxtest@yeah.net";
	private String password = "dxxtest123";
	// �ʼ������ߵĵ�ַ
	private String fromAddress = "dxxtest@yeah.net";

	// ////////////////////////////////////
	// �ʼ������ߵĵ�ַ
	private String toAddress;
	// �ʼ�����
	private String subject;
	// �ʼ����ı�����
	private String content;

	/**
	 * ����ʼ��Ự����
	 */
	public Properties getProperties() {
		Properties p = new Properties();
		p.put("mail.smtp.host", this.mailServerHost);
		p.put("mail.smtp.port", this.mailServerPort);
		p.put("mail.smtp.auth", "true");
		return p;
	}

	public MailInfo(String to, String subject, String content) {
		this.toAddress = to;
		this.subject = subject;
		this.content = content;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public void setContent(String textContent) {
		this.content = textContent;
	}
}