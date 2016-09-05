package eu.toennies.javahttpobservatoryapi;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;
import org.junit.Assert;

/**
 * An assert class to handle json objects.
 * 
 * @author Sascha Tönnies <https://github.com/stoennies>
 *
 */
public class ApiAssert extends Assert {
	
	public static void assertApiDataFetched(JSONObject apiData)
	{
		Assert.assertTrue("Could not fetch data from API", apiData.toString().length() > 2);
	}
	
	public static void assertApiResponseCode(String apiUrl, int expected)
	{
		int responseCode = -1;
				
		try
		{
			URL url = new URL(apiUrl);
			HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
			responseCode = conn.getResponseCode();
			
			conn.disconnect();
		}
		catch(IOException ignored){
			Assert.assertFalse("Failure in assertApiResponseCode method: " + ignored.getLocalizedMessage(), responseCode == -1);
		}
		
		Assert.assertFalse("Failure in assertApiResponseCode method", responseCode == -1);
		Assert.assertTrue("ResponseCode is not the expected one. (IS: " + responseCode + "; SHOULD BE: " + expected + ")", responseCode == expected);
}

}
