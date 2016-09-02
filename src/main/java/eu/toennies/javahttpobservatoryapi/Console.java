package eu.toennies.javahttpobservatoryapi;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Sascha Tönnies <https://github.com/stoennies>
 * @license GNU GENERAL PUBLIC LICENSE v3 *
 *
 */
public class Console {

	/**
	 * The main class. 
	 * @param the console parameters given to the program
	 */
	public static void main(String[] args) {
		printHeader();

		if (args.length == 1 && (args[0].equals(ApiCommands.GRADE_DISTRIBUTION.consoleCommand())
				|| args[0].equals(ApiCommands.GRADE_DISTRIBUTION.consoleShortCommand()))) {
			getGradeDistribution();
		} else if (args.length == 1 && (args[0].equals(ApiCommands.GRADE_DISTRIBUTION.consoleCommand())
				|| args[0].equals(ApiCommands.SCANNER_STATES.consoleShortCommand()))) {
			getScannerStates();
		} else if ((args.length > 0 && args.length <= 3) && (args[0].equals(ApiCommands.RECENT_SCANS.consoleCommand())
				|| args[0].equals(ApiCommands.RECENT_SCANS.consoleShortCommand()))) {
			getRecentScans(args);
		} else if ((args.length >= 2) && (args[0].equals(ApiCommands.RETRIEVE_ASSESSMENT.consoleCommand())
				|| args[0].equals(ApiCommands.RETRIEVE_ASSESSMENT.consoleShortCommand())) && args[1].contains("-host")) {
			retrieveAssessment(args);
		} else if ((args.length == 2) && (args[0].equals(ApiCommands.RETRIEVE_TEST_RESULT.consoleCommand())
				|| args[0].equals(ApiCommands.RETRIEVE_TEST_RESULT.consoleShortCommand())) && args[1].contains("-id")) {
			retrieveResults(args);
		} else {
			printUsage();
		}

	}

	/**
	 * Print out an assessment. May be cached or fresh one. 
	 * @param args - the console parameters
	 */
	private static void retrieveAssessment(String[] args) {
		String host = ConsoleUtilities.arrayValueMatchRegex(args, "-host=(.+)");

		Boolean rescan = false;
		Boolean hidden = false;
		
		for(String arg : args) {
			if(arg.contains("-rescan")) {
				rescan = true;
				
			} else if(arg.contains("-hidden")) {
				hidden = true;
			}
		}
		
		Api obervatoryApi = new Api();

		JSONObject scan = null;
		if(rescan) {
			obervatoryApi.retrieveAssessment(host, hidden);
			//Loop while observatory is working. Ask every 10 seconds
			boolean running = true;
			while(running) {
				scan = obervatoryApi.retrieveCachedAssessment(host);
				try {
					if(scan.getString("state").equalsIgnoreCase("FINISHED")) {
						running = false;
					} else {
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} else {
			scan = obervatoryApi.retrieveCachedAssessment(host);
		}
		
		Map<String, Object> map;
		try {
			map = ConsoleUtilities.jsonToMap(scan);

			System.out.println("Assessment");
			System.out.println("");
			System.out.println(ConsoleUtilities.mapToConsoleOutput(map));
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * Print out the scan result for a given id.
	 * @param args - the console parameters
	 */
	private static void retrieveResults(String[] args) {
		String id = ConsoleUtilities.arrayValueMatchRegex(args, "-id=(.+)");
		
		Api obervatoryApi = new Api();

		JSONObject recentScans = obervatoryApi.retrieveResults(id);
		Map<String, Object> map;
		try {
			map = ConsoleUtilities.jsonToMap(recentScans);

			System.out.println("Scan results");
			System.out.println("");
			System.out.println(ConsoleUtilities.mapToConsoleOutput(map));
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Print out the over all grade distribution.
	 */
	private static void getGradeDistribution() {
		Api obervatoryApi = new Api();

		JSONObject apiInfo = obervatoryApi.fetchGradeDistribution();
		Map<String, Object> map;
		try {
			map = ConsoleUtilities.jsonToMap(apiInfo);

			System.out.println("Grade distribution");
			System.out.println("");
			System.out.println(ConsoleUtilities.mapToConsoleOutput(map));
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Print out the scanner states.
	 */
	private static void getScannerStates() {
		Api obervatoryApi = new Api();

		JSONObject apiInfo = obervatoryApi.fetchScannerStates();
		Map<String, Object> map;
		try {
			map = ConsoleUtilities.jsonToMap(apiInfo);

			System.out.println("Scanner states");
			System.out.println("");
			System.out.println(ConsoleUtilities.mapToConsoleOutput(map));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Print out the recent scan results.
	 * @param args - the console parameters
	 */
	private static void getRecentScans(String[] args) {
		String max = null;
		String min = null;
		
		for(String arg : args) {
			if(arg.contains("-max")) {
				max = ConsoleUtilities.arrayValueMatchRegex(args, "-max=(.+)");
			} else if(arg.contains("-min")) {
				min = ConsoleUtilities.arrayValueMatchRegex(args, "-min=(.+)");
			}
		}
		
		Api obervatoryApi = new Api();

		JSONObject recentScans = obervatoryApi.fetchRecentScans(max, min);
		Map<String, Object> map;
		try {
			map = ConsoleUtilities.jsonToMap(recentScans);

			System.out.println("Recent scans");
			System.out.println("");
			System.out.println(ConsoleUtilities.mapToConsoleOutput(map));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Print the header.
	 */
	public static void printHeader() {
		System.out.println("");
		System.out.println(
				"     _                   ____  _                              _                            _____ _____ ");
		System.out.println(
				"    | |                 / __ \\| |                            | |                     /\\   |  __ \\_   _|");
		System.out.println(
				"    | | __ ___   ____ _| |  | | |__  ___  ___ _ ____   ____ _| |_ ___  _ __ _   _   /  \\  | |__) || |  ");
		System.out.println(
				"_   | |/ _` \\ \\ / / _` | |  | | '_ \\/ __|/ _ \\ '__\\ \\ / / _` | __/ _ \\| '__| | | | / /\\ \\ |  ___/ | |  ");
		System.out.println(
				" |__| | (_| |\\ V / (_| | |__| | |_) \\__ \\  __/ |   \\ V / (_| | || (_) | |  | |_| |/ ____ \\| |    _| |_ ");
		System.out.println(
				"\\____/ \\__,_| \\_/ \\__,_|\\____/|_.__/|___/\\___|_|    \\_/ \\__,_|\\__\\___/|_|   \\__, /_/    \\_\\_|   |_____|");
		System.out.println("by Sascha Toennies  <https://github.com/stoennies>");
		System.out.println(
				"and contributors (https://github.com/stoennies/java-http-observatory-api/graphs/contributors)");
		System.out.println(
				"-------------------------------------------------------------------------------------------------------");
		System.out.println("");
	}

	/**
	 * Print out the usage info.
	 */
	public static void printUsage() {
		String jarName = "java-http-observatory-api-" + Api.getVersion() + ".jar";
		String jarExecution = "java -jar " + jarName;

		System.out.println("Help");
		System.out.println(jarExecution);
		System.out.println("");
		System.out.println("-g, --gradeDistribution");
		System.out.println("	Retrieve overall grade distribution");
		System.out.println("");
		System.out.println("-s, --scannerStates");
		System.out.println("	Retrieve scanner states");
		System.out.println("");
		System.out.println("-r, --recentScans");
		System.out.println("	Additional parameter:");
		System.out.println("	-max (Integer) - minimum score");
		System.out.println("	-min (Integer) - maximum score");
		System.out.println("");
		System.out.println(ApiCommands.RETRIEVE_ASSESSMENT.consoleShortCommand() + ", " + ApiCommands.RETRIEVE_ASSESSMENT.consoleCommand());
		System.out.println("	Mandantory parameter:");
		System.out.println("	-host (String) - hostname");
		System.out.println("	Additional parameter:");
		System.out.println("	-rescan (boolean) - start fresh scan; default false");
		System.out.println("	-hidden (boolean) - results not shown in getRecentScans; default false");
		System.out.println("");
		System.out.println(ApiCommands.RETRIEVE_TEST_RESULT.consoleShortCommand() + ", " + ApiCommands.RETRIEVE_TEST_RESULT.consoleCommand());
		System.out.println("	Mandantory parameter:");
		System.out.println("	-id - scan id");
	}
}
