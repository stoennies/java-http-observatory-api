package eu.toennies.javahttpobservatoryapi;

import static org.junit.Assert.assertNotNull;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the api.
 * 
 * @author Sascha Tönnies <https://github.com/stoennies>
 *
 */
public class ApiTest {

	/**
	 * Init proxy if needed
	 */
	@Before
	public void init() {
		Console.getInstance();
	}
	
	/**
	 * A grade distribution call should not be null.
	 */
	@Test
	public void testFetchGradeDistribution() {
		Api api = new Api();
		JSONObject gradeDistribution = api.fetchGradeDistribution();

		assertNotNull("JSONObject is null", gradeDistribution);
		ApiAssert.assertApiDataFetched(gradeDistribution);
	}

	/**
	 * A scanner state object should not be null.
	 */
	@Test
	public void testFetchScannerStates() {
		Api api = new Api();
		JSONObject scannerStates = api.fetchScannerStates();

		assertNotNull("JSONObject is null", scannerStates);
		ApiAssert.assertApiDataFetched(scannerStates);
	}
}
