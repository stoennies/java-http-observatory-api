package eu.toennies.javahttpobservatoryapi.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import eu.toennies.javahttpobservatoryapi.ConsoleUtilities;

/**
 * Each scan consists of a variety of subtests, including Content Security
 * Policy, Subresource Integrity, etc. The results of all these tests can be
 * retrieved once the scan's state has been placed in the FINISHED state.
 * 
 * @author Sascha Tönnies <https://github.com/stoennies>
 *
 */
public class RetrieveTestResultsCommand extends ApiCommand {

	public RetrieveTestResultsCommand() {
		super("getScanResults", "retrieveTestResult", "t", "Test results",
				"Retrieves the results of the test with the given id.");

		CommandArgument param1 = new CommandArgument("id", "scan_id number from a scan object", true);
		super.addCommandArgument(param1);
	}

	@Override
	public JSONObject run(List<String> arguments) {
		if(arguments == null || ConsoleUtilities.listValueMatchRegex(arguments, "id=(.+)") == null) {
			throw new IllegalArgumentException("The mandantory argument id is not given.");
		}

		String id = ConsoleUtilities.listValueMatchRegex(arguments, "id=(.+)");
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("scan", id);

		return super.callApiCommand(parameters);
	}
}