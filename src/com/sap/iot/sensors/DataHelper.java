package com.sap.iot.sensors;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.persistence.Measurement;

public class DataHelper {
	private EntityManagerFactory emf;

	public DataHelper(EntityManagerFactory emf) {
		this.emf = emf;
	}

	/*
	 * 
	 * Persists a measured sensor value (measurement)
	 * 
	 * 
	 * 
	 * @param measurement The measured sensor value
	 */
	public boolean addMeasurement(Measurement measurement) {
		boolean result = false;
		EntityManager em = emf.createEntityManager();
		// System.out.println("Trying to commit sensor data for sensor " +
		// measurement.getSensorDescription());
		try {
			if (measurement != null && measurement.getValue() != null) {
				em.getTransaction().begin();
				em.persist(measurement);
				em.getTransaction().commit();
			}
		} catch (Exception e) {
			System.out.println("ERROR: persisting measurement didn't work "
					+ e.getMessage());
		} finally {
			em.close();
		}
		return result;
	}

	/*
	 * 
	 * Provides a list of ALL sensor readings. To avoid too many data the output
	 * 
	 * is restricted to a maximum of 500 entries
	 */
	@SuppressWarnings("unchecked")
	public List<Measurement> getAllSensorReadings() {
		List<Measurement> result = null;
		EntityManager em = emf.createEntityManager();
		try {
			Query q = em.createNamedQuery("AllMeasurements");
			q.setMaxResults(500);
			result = q.getResultList();
		} catch (Exception e) {
		}
		em.close();
		return result;
	}

	/*
	 * 
	 * Provides the last measured sensor value for a sensor
	 * 
	 * 
	 * 
	 * @param sensorId The sensor id of the sensor that you wish to get the
	 * 
	 * measured value from
	 */
	public Measurement getLastSensorReading(long sensorId) {
		Measurement result = null;
		EntityManager em = emf.createEntityManager();
		try {
			Query q = em.createNamedQuery("LastSensorReading");
			q.setParameter("paramSensorId", sensorId);
			result = (Measurement) q.getSingleResult();
		} catch (Exception e) {
		}
		em.close();
		return result;
	}
}
