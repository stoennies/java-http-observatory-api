package eu.toennies.javahttpobservatoryapi;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import eu.toennies.javahttpobservatoryapi.commands.GradeDistributionCommand;
import eu.toennies.javahttpobservatoryapi.commands.InvokeAssessmentCommand;
import eu.toennies.javahttpobservatoryapi.commands.RecentScansCommand;
import eu.toennies.javahttpobservatoryapi.commands.RetrieveAssessmentCommand;
import eu.toennies.javahttpobservatoryapi.commands.RetrieveTestResultsCommand;
import eu.toennies.javahttpobservatoryapi.commands.ScannerStatesCommand;

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
	public void testGradeDistributionCommand() {
		GradeDistributionCommand cmd = new GradeDistributionCommand();
		JSONObject gradeDistribution = cmd.run(null);

		assertNotNull("JSONObject is null", gradeDistribution);
		ApiAssert.assertApiDataFetched(gradeDistribution);
	}

	/**
	 * A scanner state object should not be null and have at least the FINISHED
	 * and FAILED states.
	 */
	@Test
	public void testScannerStatesCommand() {
		ScannerStatesCommand cmd = new ScannerStatesCommand();
		JSONObject scannerStates = cmd.run(null);

		assertNotNull("JSONObject is null", scannerStates);
		assertTrue("Scanner state object does not contain status FAILED", scannerStates.has("FAILED"));
		assertTrue("Scanner state object does not contain status FINISHED", scannerStates.has("FINISHED"));
		ApiAssert.assertApiDataFetched(scannerStates);
	}

	/**
	 * A assessment on google.com should not be null.
	 */
	@Test
	public void testInvokeAssessmentCommand() {
		InvokeAssessmentCommand cmd = new InvokeAssessmentCommand();

		JSONObject assessment = new JSONObject();
		try {
			// Argument host is mandatory. Should fail
			assessment = cmd.run(null);
			fail("Should have raised an IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			List<String> arguments = new ArrayList<String>();
			arguments.add("host=google.com");
			arguments.add("hidden");
			assessment = cmd.run(arguments);

			assertNotNull("JSONObject is null", assessment);
			ApiAssert.assertApiDataFetched(assessment);
		}
	}

	/**
	 * A assessment on google.com should not be null.
	 */
	@Test
	public void testRetrieveAssessmentCommand() {
		RetrieveAssessmentCommand cmd = new RetrieveAssessmentCommand();
		List<String> arguments = new ArrayList<String>();
		arguments.add("host=google.com");
		JSONObject assessment = cmd.run(arguments);

		assertNotNull("JSONObject is null", assessment);
		ApiAssert.assertApiDataFetched(assessment);
	}

	@Test
	public void testRetrieveTestResultsCommand() {
		RetrieveTestResultsCommand cmd = new RetrieveTestResultsCommand();
		try {
			// Argument id is mandatory. Should fail
			cmd.run(null);
			fail("Should have raised an IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			return;
		}
	}

	/**
	 * A recent scan call should not be null.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testRecentScansCommand() {
		RecentScansCommand cmd = new RecentScansCommand();
		JSONObject json = cmd.run(null);
		assertNotNull("JSONObject is null", json);
		ApiAssert.assertApiDataFetched(json);

		// Test the min argument
		List<String> arguments = new ArrayList<String>();
		arguments.add("min=100");
		json = cmd.run(arguments);
		String key;
		for (Iterator<String> it = json.keys(); it.hasNext();) {
			key = it.next();
			try {
				assertTrue("Calling recent scan with a minimum score of 100 should only retrieve A+ score",
						json.get(key).equals("A+"));
			} catch (JSONException e) {
				fail();
			}
		}

		arguments.clear();
		arguments.add("max=80");
		json = cmd.run(arguments);
		for (Iterator<String> it = json.keys(); it.hasNext();) {
			try {
				key = it.next();
				assertFalse("Calling recent scan with a maximum score of 80 should only retrieve A scores",
						json.get(key).equals("A-"));
				assertFalse("Calling recent scan with a maximum score of 80 should only retrieve A scores",
						json.get(key).equals("A"));
				assertFalse("Calling recent scan with a maximum score of 80 should only retrieve A scores",
						json.get(key).equals("A+"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}
}
