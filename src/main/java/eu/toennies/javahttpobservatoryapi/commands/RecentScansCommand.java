package eu.toennies.javahttpobservatoryapi.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import eu.toennies.javahttpobservatoryapi.ConsoleUtilities;

/**
 * Retrieve the ten most recent scans that fall within a given score range. Maps
 * hostnames to scores.
 * 
 * Example for a recent scans object
 * 
 * { "site1.mozilla.org": "A", "site2.mozilla.org": "B-", "site3.mozilla.org":
 * "C+", "site4.mozilla.org": "F", "site5.mozilla.org": "F",
 * "site6.mozilla.org": "E", "site7.mozilla.org": "F", "site8.mozilla.org":
 * "B+", "site9.mozilla.org": "A+", "site0.mozilla.org": "A-" } *
 * 
 * @author Sascha Tönnies <https://github.com/stoennies>
 *
 */
public class RecentScansCommand extends ApiCommand {

	public RecentScansCommand() {
		super("getRecentScans", "recentScans", "r", "Recent scans",
				"Retrieve the ten most recent scans that fall within a given score range.");

		CommandArgument param1 = new CommandArgument("max", "maximum score", false);
		super.addCommandArgument(param1);
		CommandArgument param2 = new CommandArgument("min", "minimum score", false);
		super.addCommandArgument(param2);
	}

	@Override
	public JSONObject run(List<String> arguments) {
		Map<String, String> parameters = new HashMap<String, String>();
		if(arguments != null) {
			String max = ConsoleUtilities.listValueMatchRegex(arguments, "max=(.+)");
			String min = ConsoleUtilities.listValueMatchRegex(arguments, "min=(.+)");

			parameters.put("max", max);
			parameters.put("min", min);
		}

		return super.callApiCommand(parameters);
	}
}