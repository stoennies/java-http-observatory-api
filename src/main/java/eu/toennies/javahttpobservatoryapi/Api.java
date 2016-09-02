package eu.toennies.javahttpobservatoryapi;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

/**
 * Java-SSLLabs-API
 * 
 * This Java library provides basic access to Mozilla's Observatory API and is
 * build upon the official API documentation at
 * https://github.com/mozilla/http-observatory/blob/master/httpobs/docs/api.md
 * 
 * @author Sascha Tönnies <https://github.com/stoennies>
 * @license GNU GENERAL PUBLIC LICENSE v3
 */
public class Api {
	private static final String API_URL = "https://http-observatory.security.mozilla.org/api/v1";
	private static final String VERSION = "0.0.1-SNAPSHOT";

	/**
	 * Getter for VERSION
	 * 
	 * @return String
	 */
	public static String getVersion() {
		return VERSION;
	}

	/**
	 * This returns each possible grade in the HTTP Observatory, as well as how
	 * many scans have fallen into that grade.
	 * 
	 * @return grade distribution object
	 * 
	 *         { "A+": 3, "A": 6, "A-": 2, "B+": 8, "B": 76, "B-": 79, "C+": 80,
	 *         "C": 88, "C-": 86, "D+": 60, "D": 110, "D-": 215, "E": 298, "F":
	 *         46770 }
	 */
	public JSONObject fetchGradeDistribution() {
		String jsonString;
		JSONObject json = new JSONObject();

		try {
			jsonString = sendApiGetRequest(ApiCommands.SCANNER_STATES.apiCommand(), null);
			json = new JSONObject(jsonString);
		} catch (Exception ignored) {
		}

		return (json);
	}

	/**
	 * This returns the state of the scanner. It can be useful for determining
	 * how busy the HTTP Observatory is.
	 * 
	 * @return a scanner state object
	 * 
	 *         e.g. { "ABORTED": 10, "FAILED": 281, "FINISHED": 46240,
	 *         "PENDING": 122, "STARTING": 96, "RUNNING: 128, }
	 */
	public JSONObject fetchScannerStates() {
		String jsonString;
		JSONObject json = new JSONObject();

		try {
			jsonString = sendApiGetRequest(ApiCommands.SCANNER_STATES.apiCommand(), null);
			json = new JSONObject(jsonString);
		} catch (Exception ignored) {
		}

		return (json);
	}

	/**
	 * Retrieve the ten most recent scans that fall within a given score range.
	 * Maps hostnames to scores.
	 * 
	 * @param max
	 *            - maximum score
	 * @param min
	 *            - minimum score
	 * @return a recent scans object
	 * 
	 *         { "site1.mozilla.org": "A", "site2.mozilla.org": "B-",
	 *         "site3.mozilla.org": "C+", "site4.mozilla.org": "F",
	 *         "site5.mozilla.org": "F", "site6.mozilla.org": "E",
	 *         "site7.mozilla.org": "F", "site8.mozilla.org": "B+",
	 *         "site9.mozilla.org": "A+", "site0.mozilla.org": "A-" }
	 */
	/**
	 * @param max
	 * @param min
	 * @return
	 */
	public JSONObject fetchRecentScans(final String max, final String min) {
		String jsonString;
		JSONObject json = new JSONObject();

		try {
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("max", max);
			parameters.put("min", min);

			jsonString = sendApiGetRequest(ApiCommands.RECENT_SCANS.apiCommand(), parameters);
			json = new JSONObject(jsonString);
		} catch (Exception ignored) {
		}

		return (json);
	}

	/**
	 * This is used to retrieve the results of an existing, ongoing, or
	 * completed scan.
	 * 
	 * @param hostname
	 *            - the host to retrieve the assessment for
	 * @return a scan object
	 * 
	 *         { "end_time": "Tue, 22 Mar 2016 21:51:41 GMT", "grade": "A",
	 *         "response_headers": { ... }, "scan_id": 1, "score": 90,
	 *         "start_time": "Tue, 22 Mar 2016 21:51:40 GMT", "state":
	 *         "FINISHED", "tests_failed": 2, "tests_passed": 9,
	 *         "tests_quantity": 11 }
	 * 
	 */
	public JSONObject retrieveCachedAssessment(final String hostname) {
		String jsonString;
		JSONObject json = new JSONObject();

		try {
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("host", hostname);

			jsonString = sendApiGetRequest(ApiCommands.RETRIEVE_ASSESSMENT.apiCommand(), parameters);
			json = new JSONObject(jsonString);
		} catch (Exception ignored) {
		}

		return (json);
	}

	public JSONObject retrieveAssessment(final String hostname, final Boolean hidden) {
		String jsonString;
		JSONObject json = new JSONObject();

		try {
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("hidden", hidden.toString());
			parameters.put("rescan", "true");

			jsonString = sendApiPostRequest(ApiCommands.RETRIEVE_ASSESSMENT.apiCommand()+"?host="+hostname, parameters);
			json = new JSONObject(jsonString);
		} catch (Exception ignored) {
		}

		return (json);
	}

	/**
	 * Each scan consists of a variety of subtests, including Content Security
	 * Policy, Subresource Integrity, etc. The results of all these tests can be
	 * retrieved once the scan's state has been placed in the FINISHED state.
	 * 
	 * @param id - the scan id
	 * @return a single test object
	 * 
	 */
	public JSONObject retrieveResults(final String id) {
		String jsonString;
		JSONObject json = new JSONObject();

		try {
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("scan", id);

			jsonString = sendApiGetRequest(ApiCommands.RETRIEVE_TEST_RESULT.apiCommand(), parameters);
			json = new JSONObject(jsonString);
		} catch (Exception ignored) {
		}

		return (json);
	}

	/**
	 * Sends an api request and return api response
	 * 
	 * @param apiCall
	 * @param parameters
	 * @return String
	 * @throws IOException
	 */
	private String sendApiGetRequest(String apiCall, Map<String, String> parameters) throws IOException {
		URL url = new URL(API_URL + "/" + apiCall);

		if (parameters != null) {
			url = new URL(url.toString() + buildGetParameterString(parameters));
		}

		// TODO: Proxy settings via parameter
		// InputStream is;
		// if (Config.getInstance().isUseProxy()) {
		// Proxy proxy = new Proxy(Proxy.Type.HTTP, new
		// InetSocketAddress(Config.getInstance().getProxyIp(),
		// Config.getInstance().getProxyPort()));
		// is = ((HttpURLConnection)
		// url.openConnection(proxy)).getInputStream();
		// } else {
		// is = ((HttpURLConnection) url.openConnection()).getInputStream();
		// }
		// while ((nextByteOfData = is.read()) != -1) {
		// apiResponseBuffer.append((char) nextByteOfData);
		// }
		// is.close();

		InputStream is = url.openStream();
		int nextByteOfData = 0;

		StringBuffer apiResponseBuffer = new StringBuffer();

		while ((nextByteOfData = is.read()) != -1) {
			apiResponseBuffer.append((char) nextByteOfData);
		}

		is.close();

		return (apiResponseBuffer.toString());
	}

	private String sendApiPostRequest(String apiCall, Map<String, String> parameters) throws IOException {
		URL url = new URL(API_URL + "/" + apiCall);

		String urlParameters = buildGetParameterString(parameters);
		byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
		int    postDataLength = postData.length;

		HttpsURLConnection conn= (HttpsURLConnection) url.openConnection();           
		conn.setDoOutput( true );
		conn.setInstanceFollowRedirects( false );
		conn.setRequestMethod( "POST" );
		conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
		conn.setRequestProperty( "charset", "utf-8");
		conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
		conn.setUseCaches( false );
		
		
		
		try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
		   wr.write( postData );
		}
		
		
		InputStream is = conn.getInputStream();
		int nextByteOfData = 0;

		StringBuffer apiResponseBuffer = new StringBuffer();

		while ((nextByteOfData = is.read()) != -1) {
			apiResponseBuffer.append((char) nextByteOfData);
		}

		is.close();
		conn.disconnect();

		return (apiResponseBuffer.toString());
	}

	/**
	 * Helper function to build GET parameter string
	 * 
	 * @param parameters
	 * @return String
	 */
	private String buildGetParameterString(Map<String, String> parameters) {
		String getParameterString = "";

		for (Map.Entry<String, String> param : parameters.entrySet()) {
			if (param.getValue() == null) {
				continue;
			}

			getParameterString += (getParameterString.length() < 1) ? ("?") : ("&");

			getParameterString += param.getKey() + "=" + param.getValue();
		}

		return (getParameterString);
	}

}