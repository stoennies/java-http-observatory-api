package eu.toennies.javahttpobservatoryapi.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

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
public class RetrieveAssessmentCommand extends ApiCommand {

	public RetrieveAssessmentCommand() {
		super("analyze", "retrieveAssessment", "ra",
				"Retrieve assessment", "This is used to retrieve the results of an existing, ongoing, or completed scan. ");

		CommandArgument param1 = new CommandArgument("host", "hostname to scan", true);
		super.addCommandArgument(param1);

	}

	@Override
	public JSONObject run(List<String> arguments) throws IllegalArgumentException {
		if (arguments == null || ConsoleUtilities.listValueMatchRegex(arguments, "host=(.+)") == null) {
			throw new IllegalArgumentException("The mandatory argument host is not given.");
		}

		String host = ConsoleUtilities.listValueMatchRegex(arguments, "host=(.+)");
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("host", host);

		return super.callApiCommand(parameters);
	}
}