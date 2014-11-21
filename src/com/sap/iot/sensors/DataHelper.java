package com.sap.iot.sensors;

import java.util.Collections;
import java.util.Comparator;
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

	public boolean addMeasurement(Measurement measurement) {
		boolean result = false;
		EntityManager em = emf.createEntityManager();

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

	@SuppressWarnings("unchecked")
	public List<Measurement> getLastDeviceMeasurements(String username,
			String device, String metric) {
		List<Measurement> result = null;
		EntityManager em = emf.createEntityManager();
		try {

			Query q = em.createNamedQuery("LastDeviceMeasurements");
			q.setParameter("pUsername", username);
			q.setParameter("pDevice", device);
			q.setParameter("pMetric", metric);

			q.setMaxResults(20);
			result = q.getResultList();

			Collections.sort(result, new Comparator<Measurement>() {
				public int compare(Measurement m1, Measurement m2) {
					return m1.getTimestp().compareTo(m2.getTimestp());
				}
			});

		} catch (Exception e) {

		}

		em.close();
		return result;
	}
}
