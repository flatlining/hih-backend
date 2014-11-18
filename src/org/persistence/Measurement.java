package org.persistence;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@IdClass(Measurement.class)
@NamedQueries({ @NamedQuery(name = "AllMeasurements", query = "select m from Measurement m") })
public class Measurement implements Serializable {

	private static final long serialVersionUID = 1L;

	public Measurement() {
	}

	@Id
	private Timestamp timestp;
	@Id
	private String username;
	@Id
	private String device;
	@Id
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

	public boolean equals(Object o) {
		return ((o instanceof Measurement) && this.hashCode() == ((Measurement) o)
				.hashCode());
	}

	public int hashCode() {
		return (timestp.toString() + username + device + metric).hashCode();
	}
}