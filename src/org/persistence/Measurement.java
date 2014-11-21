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
@NamedQueries({ @NamedQuery(name = "LastDeviceMeasurements", query = "select m from Measurement m where m.username = :pUsername and m.device = :pDevice and m.metric = :pMetric and m.timestp =  (SELECT MAX(r.timestp) from Measurement r where r.username = :pUsername and r.device = :pDevice and r.metric = :pMetric)") })
public class Measurement implements Serializable {

	private static final long serialVersionUID = 1L;

	public Measurement() {
	}

	@Id
	@GeneratedValue(strategy = AUTO)
	private long id;
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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