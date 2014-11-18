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

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String action = encodeText(request.getParameter("action"));
		DataHelper dataHelper = new DataHelper(emf);

		if (action != null && action.equalsIgnoreCase("addmeasurement")) {
			Measurement measurement = extractMeasurementData(request);
			dataHelper.addMeasurement(measurement);
		}
	}

	private Measurement extractMeasurementData(HttpServletRequest request) {
		Measurement measurement = new Measurement();

		String username = encodeText(request.getParameter("username"));
		String device = encodeText(request.getParameter("device"));
		String metric = encodeText(request.getParameter("metric"));
		String value = encodeText(request.getParameter("value"));

		if (username != null && username.length() > 0 && device != null
				&& device.length() > 0 && metric != null && metric.length() > 0
				&& value != null && value.length() > 0) {
			measurement.setTimestp(new Timestamp(new Date().getTime()));
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