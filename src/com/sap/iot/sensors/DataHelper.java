package com.sap.iot.sensors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

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
}
