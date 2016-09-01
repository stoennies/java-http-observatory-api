package eu.toennies.javahttpobservatoryapi;

import org.json.JSONObject;
import org.junit.Test;

public class ApiTest {

	/**
	 * A grade distribution call should not be null.
	 */
	@Test
	public void testFetchGradeDistribution() {
		Api api = new Api();
		JSONObject gradeDistribution = api.fetchGradeDistribution();

		ApiAssert.assertNotNull("JSONObject is null", gradeDistribution);
		ApiAssert.assertApiDataFetched(gradeDistribution);
	}

	/**
	 * A scanner state object should not be null.
	 */
	@Test
	public void testFetchScannerStates() {
		Api api = new Api();
		JSONObject scannerStates = api.fetchScannerStates();

		ApiAssert.assertNotNull("JSONObject is null", scannerStates);
		ApiAssert.assertApiDataFetched(scannerStates);
	}
}
