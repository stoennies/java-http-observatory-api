package eu.toennies.javahttpobservatoryapi.commands;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import eu.toennies.javahttpobservatoryapi.Api;
import eu.toennies.javahttpobservatoryapi.ConsoleUtilities;

/**
 * This is used to retrieve the results of an existing, ongoing, or completed
 * scan.
 * 
 * Example scan object
 * 
 * { "end_time": "Tue, 22 Mar 2016 21:51:41 GMT", "grade": "A",
 * "response_headers": { ... }, "scan_id": 1, "score": 90, "start_time":
 * "Tue, 22 Mar 2016 21:51:40 GMT", "state": "FINISHED", "tests_failed": 2,
 * "tests_passed": 9, "tests_quantity": 11 }
 * 
 * @author Sascha Tönnies <https://github.com/stoennies>
 *
 */
public class InvokeAssessmentCommand extends ApiCommand {

	public InvokeAssessmentCommand() {
		super("analyze", "retrieveAssessment", "ia", "Invoke assessment",
				"Used to invoke a new scan of a website. By default, the HTTP Observatory will return a cached site result if the site has been scanned anytime in the previous 24 hours.");

		CommandArgument param1 = new CommandArgument("host", "hostname to scan", true);
		super.addCommandArgument(param1);
		CommandArgument param2 = new CommandArgument("rescan", "true if a fresh scan should be performed", false);
		super.addCommandArgument(param2);
		CommandArgument param3 = new CommandArgument("hidden",
				"true if the result should not show up in the getRecenScan call", false);
		super.addCommandArgument(param3);

	}

	@Override
	public JSONObject run(List<String> arguments) throws IllegalArgumentException {
		if(arguments == null || ConsoleUtilities.listValueMatchRegex(arguments, "host=(.+)") == null) {
			throw new IllegalArgumentException("The mandatory argument host is not given.");
		}

		JSONObject json = null;
		try {
			Api api = new Api();
			
			Map<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("rescan", arguments.contains("rescan") ? "true" : "false");
			postParameters.put("hidden", arguments.contains("hidden") ? "true" : "false");
			
			String host = ConsoleUtilities.listValueMatchRegex(arguments, "host=(.+)");
			final String commandUrl = getApiCommand() + "?host=" + host;

			json = new JSONObject(api.sendApiPostRequest(commandUrl, postParameters));
			checkForError(json);
		} catch (JSONException e) {
			Logger.getGlobal().severe("Could not build result: " + e.getLocalizedMessage());
		} catch (IOException e) {
			Logger.getGlobal().severe("Could not send API request: " + e.getLocalizedMessage());
		}

		return json;
	}

	private void checkForError(JSONObject json) throws IOException, JSONException {
		if (json.has("error")) {
			String error = json.getString("error");
			if ("rescan-attempt-too-soon".equals(error)) {
				throw new IOException(
						"A resacan attempt to soon. Try calling without \"rescan\" or wait 5 minutes.");
			}
		}
	}
}