package org.persistence;

import static javax.persistence.GenerationType.AUTO;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({ @NamedQuery(name = "AllMeasurements", query = "select m from Measurement m") })
public class Measurement implements Serializable {

	private static final long serialVersionUID = 1L;

	public Measurement() {
	}

	@Id
	@GeneratedValue(strategy = AUTO)
	private Timestamp timestp;
	private String username;
	private String device;
	private String metric;
	private Long value;

	public Timestamp getTimestp() {
		return timestp;
	}

	public void setTimestp(Timestamp timestp) {
		this.timestp = timestp;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}