package com.sap.iot.sensors;

// http://scn.sap.com/community/developer-center/cloud-platform/blog/2014/11/17/raspberrypi-on-sap-hcp--iot-blog-series-part-2-receiving-the-sensor-values-in-the-cloud

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.persistence.Measurement;

import com.google.gson.Gson;
import com.sap.security.core.server.csi.IXSSEncoder;
import com.sap.security.core.server.csi.XSSEncoder;

/**
 * 
 * Servlet implementation class SensorsServlet
 */
public class SensorsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DataSource ds;
	private EntityManagerFactory emf;

	/**
	 * 
	 * @see HttpServlet#HttpServlet()
	 */
	public SensorsServlet() {
		super();
	}

	/**
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String action = encodeText(request.getParameter("action"));
		DataHelper dataHelper = new DataHelper(emf);
		// Add a sensor value
		if (action != null && action.equalsIgnoreCase("addsensorvalue")) {
			Measurement measurement = extractMeasurementData(request);
			dataHelper.addMeasurement(measurement);
		}
		// Provide a JSON output of all sensor values (measurements)
		if (action != null && action.equalsIgnoreCase("showallmeasurements")) {
			List<Measurement> sensorMeasurements = dataHelper
					.getAllSensorReadings();
			outputJsonForAllMeasurements(response, sensorMeasurements);
		}
	}

	/*
	 * 
	 * Creates a JSON output of all sensor values (measurements)
	 * 
	 * 
	 * 
	 * @param response The HTTP-response object
	 * 
	 * 
	 * 
	 * @param sensorMeasurements The list of sensors values (measurements)
	 */
	private void outputJsonForAllMeasurements(HttpServletResponse response,
			List<Measurement> sensorMeasurements) {
		Gson gson = new Gson();
		try {
			response.getWriter().println("{");
			for (int i = 0; i < sensorMeasurements.size(); i++) {
				response.getWriter().println(
						gson.toJson(sensorMeasurements.get(i)));
				if (i != sensorMeasurements.size() - 1) {
					response.getWriter().println(",");
				}
			}
			response.getWriter().println("}");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 
	 * Extracts a Measurement object (sensor values) out of the parameters
	 * 
	 * provided in the HTTP-request
	 * 
	 * 
	 * 
	 * @param request The HTTP-request object
	 * 
	 * 
	 * 
	 * @return The derived Measurement object
	 */
	private Measurement extractMeasurementData(HttpServletRequest request) {
		Measurement measurement = new Measurement();
		// Get sensorId
		String sensorIdString = encodeText(request.getParameter("sensorid"));
		if (sensorIdString != null && sensorIdString.length() > 0) {
			// measurement.setSensorId(Long.parseLong(sensorIdString));
		}
		// Get unit of measured value
		// measurement.setUnit(encodeText(request.getParameter("unit")));
		// Get measured value and calculate the value to be stored
		String sensorValue = encodeText(request.getParameter("sensorvalue"));
		String sensorValueMultiplier = encodeText(request
				.getParameter("sensorvaluemultiplier"));
		String sensorValueCalibration = encodeText(request
				.getParameter("sensorvaluecalibration"));
		if (sensorValueCalibration != null
				&& sensorValueCalibration.length() > 0 && sensorValue != null
				&& sensorValueMultiplier != null
				&& sensorValueMultiplier.length() > 0
				&& sensorValue.length() > 0) {
			// measurement.setStoredAt(new Timestamp(new Date().getTime()));
			Double valueDouble = Double.parseDouble(sensorValue);
			Double multiplierDouble = Double.parseDouble(sensorValueMultiplier);
			Double valueGap = Double.parseDouble(sensorValueCalibration);
			Double value = (valueDouble * multiplierDouble) + valueGap;
			// measurement.setValue(value);
		}
		return measurement;
	}

	/*
	 * 
	 * Encodes a text to avoid cross-site-scripting vulnerability
	 * 
	 * 
	 * 
	 * @param request The text to be encoded
	 * 
	 * 
	 * 
	 * @return The encoded String
	 */
	private String encodeText(String text) {
		String result = null;
		IXSSEncoder xssEncoder = XSSEncoder.getInstance();
		try {
			result = (String) xssEncoder.encodeURL(text);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

	/** {@inheritDoc} */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void init() throws ServletException {
		try {
			InitialContext ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/DefaultDB");
			Map properties = new HashMap();
			properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, ds);
			emf = Persistence.createEntityManagerFactory("iotscenario",
					properties);
		} catch (NamingException e) {
			throw new ServletException(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void destroy() {
		emf.close();
	}
}