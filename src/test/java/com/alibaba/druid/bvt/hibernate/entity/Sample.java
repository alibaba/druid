package com.alibaba.druid.bvt.hibernate.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author yinheli [yinheli@gmail.com]
 */
@Entity
@Table(name = "SAMPLE")
public class Sample implements Serializable {

	@Id
	private Long id;

	private String name;

	private String desc;

	private Date createTime;

	private Date updateTime;

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
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

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "Sample{" +
				"createTime=" + createTime +
				", id=" + id +
				", name='" + name + '\'' +
				", desc='" + desc + '\'' +
				", updateTime=" + updateTime +
				'}';
	}
}
