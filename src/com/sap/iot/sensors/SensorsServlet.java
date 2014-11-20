package com.sap.iot.sensors;

// http://scn.sap.com/community/developer-center/cloud-platform/blog/2014/11/17/raspberrypi-on-sap-hcp--iot-blog-series-part-2-receiving-the-sensor-values-in-the-cloud

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
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

import com.sap.security.core.server.csi.IXSSEncoder;
import com.sap.security.core.server.csi.XSSEncoder;

public class SensorsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DataSource ds;
	private EntityManagerFactory emf;

	public SensorsServlet() {
		super();
	}

	@Override
	public void destroy() {
		emf.close();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init() throws ServletException {
		try {
			InitialContext ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/DefaultDB");

			@SuppressWarnings("rawtypes")
			Map properties = new HashMap();
			properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, ds);
			emf = Persistence.createEntityManagerFactory("hihbe", properties);
		} catch (NamingException e) {
			throw new ServletException(e);
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// response.getWriter().write("Health in HANA\n");
		String action = encodeText(request.getParameter("action"));
		DataHelper dataHelper = new DataHelper(emf);
		
		action = "addmeasurement";

		if (action != null && action.equalsIgnoreCase("addmeasurement")) {
			Measurement measurement = extractMeasurementData(request);
			dataHelper.addMeasurement(measurement);
			response.getWriter().write("1");
		}
	}

	private Measurement extractMeasurementData(HttpServletRequest request) {
		Measurement measurement = new Measurement();

		Timestamp timestp = new Timestamp(new Date().getTime());
		String username = encodeText(request.getParameter("username"));
		String device = encodeText(request.getParameter("device"));
		String metric = encodeText(request.getParameter("metric"));
		String value = encodeText(request.getParameter("value"));

		if (username != null && username.length() > 0 && device != null
				&& device.length() > 0 && metric != null && metric.length() > 0
				&& value != null && value.length() > 0) {
			measurement.setTimestp(timestp);
			measurement.setUsername(username);
			measurement.setDevice(device);
			measurement.setMetric(metric);
			measurement.setValue(Long.valueOf(value));
		}
		return measurement;
	}

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

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
}