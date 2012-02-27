/**
 * Ad.java 3:55:00 PM Jan 6, 2012
 *
 * Copyright(c) 2000-2012 HC360.COM, All Rights Reserved.
 */
package po;

import java.io.Serializable;
import java.util.Date;

import dao.annotation.Column;
import dao.annotation.Table;
import dao.annotation.Transient;

/**
 * �����Ϣʵ����
 * 
 * @author dixingxing
 * @date Jan 6, 2012
 */
@Table(name = "AD")
public class Ad implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	// ����ʾ
	private String name;
	// �������
	private String detail;
	// ��ʾλ��
	private Long position;
	// ״̬
	private Long state;
	// ����ʱ��
	@Column(updatable = false)
	private Date publishTime;
	// ������
	@Column(updatable = false)
	private String publishMan;
	// �޸�ʱ��
	private Date updateTime;
	// �޸���
	private String updateMan;

	private Long providerId;

	// ������� ���Զ���ѯ
	@Transient
	private String providerName;

	public Long getProviderId() {
		return providerId;
	}

	public void setProviderId(Long providerId) {
		this.providerId = providerId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Long getPosition() {
		return position;
	}

	public void setPosition(Long position) {
		this.position = position;
	}

	public Long getState() {
		return state;
	}

	public void setState(Long state) {
		this.state = state;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public String getPublishMan() {
		return publishMan;
	}

	public void setPublishMan(String publishMan) {
		this.publishMan = publishMan;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateMan() {
		return updateMan;
	}

	public void setUpdateMan(String updateMan) {
		this.updateMan = updateMan;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id:").append(id).append(",").append("name:").append(name)
				.append(",").append("publishMan:").append(publishMan).append(
						",publishTime:").append(publishTime);
		return sb.toString();
	}
}
