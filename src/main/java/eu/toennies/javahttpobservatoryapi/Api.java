package eu.toennies.javahttpobservatoryapi;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;

/**
 * This Java library provides basic access to Mozilla's Observatory API and is
 * build upon the official API documentation at
 * https://github.com/mozilla/http-observatory/blob/master/httpobs/docs/api.md
 * 
 * @author Sascha Tönnies <https://github.com/stoennies>
 */
public class Api {
	private static final String API_URL = "https://http-observatory.security.mozilla.org/api/v1";
	private static Properties PROP = new Properties();
	
	static {
		InputStream resourceAsStream = Api.class.getResourceAsStream("/version.properties");
		try {
			PROP.load(resourceAsStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				resourceAsStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * The version
	 * 
	 * @return String
	 */
	public static String getVersion() {
		return PROP.getProperty("version");
	}
	
	/**
	 * Sends an api request and return api response
	 * 
	 * @param apiCall
	 * @param parameters
	 * @return String
	 * @throws IOException
	 */
	public String sendApiGetRequest(String apiCall, Map<String, String> parameters) throws IOException {
		URL url = new URL(API_URL + "/" + apiCall);

		if (parameters != null) {
			url = new URL(url.toString() + buildGetParameterString(parameters));
		}

		InputStream is;
		if (Console.getInstance().getProxy() == null) {
			is = url.openStream();
		} else {
			Proxy proxy = new Proxy(Proxy.Type.HTTP,
					new InetSocketAddress(Console.getInstance().getProxy().split(":")[0],
							Integer.parseInt(Console.getInstance().getProxy().split(":")[1])));
			is = ((HttpURLConnection) url.openConnection(proxy)).getInputStream();
		}

		int nextByteOfData = 0;
		StringBuffer apiResponseBuffer = new StringBuffer();
		while ((nextByteOfData = is.read()) != -1) {
			apiResponseBuffer.append((char) nextByteOfData);
		}
		is.close();

		return apiResponseBuffer.toString();
	}

	public String sendApiPostRequest(String apiCall, Map<String, String> parameters) throws IOException {
		URL url = new URL(API_URL + "/" + apiCall);

		String urlParameters = buildGetParameterString(parameters);
		byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
		int postDataLength = postData.length;

		HttpsURLConnection conn;
		if (Console.getInstance().getProxy() == null) {
			conn = (HttpsURLConnection) url.openConnection();
		} else {
			Proxy proxy = new Proxy(Proxy.Type.HTTP,
					new InetSocketAddress(Console.getInstance().getProxy().split(":")[0],
							Integer.parseInt(Console.getInstance().getProxy().split(":")[1])));
			conn = (HttpsURLConnection) url.openConnection(proxy);
		}

		conn.setDoOutput(true);
		conn.setInstanceFollowRedirects(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("charset", "utf-8");
		conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
		conn.setUseCaches(false);

		try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
			wr.write(postData);
		}

		InputStream is = conn.getInputStream();
		int nextByteOfData = 0;

		StringBuffer apiResponseBuffer = new StringBuffer();

		while ((nextByteOfData = is.read()) != -1) {
			apiResponseBuffer.append((char) nextByteOfData);
		}

		is.close();
		conn.disconnect();

		return apiResponseBuffer.toString();
	}

	/**
	 * Helper function to build GET parameter string
	 * 
	 * @param parameters
	 * @return String
	 */
	private String buildGetParameterString(Map<String, String> parameters) {
		StringBuffer getParameterString = new StringBuffer();

		for (Map.Entry<String, String> param : parameters.entrySet()) {
			if (param.getValue() == null) {
				continue;
			}

			getParameterString.append(getParameterString.length() < 1 ? "?" : "&");
			getParameterString.append(param.getKey());
			getParameterString.append("=");
			getParameterString.append(param.getValue());
		}

		return getParameterString.toString();
	}

}