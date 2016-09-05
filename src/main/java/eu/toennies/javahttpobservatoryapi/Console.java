package eu.toennies.javahttpobservatoryapi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The console class. Starting point for the programm.
 * 
 * @author Sascha Tönnies <https://github.com/stoennies>
 *
 */
public class Console {

	/**
	 * singelton instance
	 */
	private static final Console CONSOLE = new Console();

	private String proxy = null;

	public String getProxy() {
		return proxy;
	}

	private Console() {
		configureProxy();
	}

	public static Console getInstance() {
		return CONSOLE;
	}

	/**
	 * The main class.
	 * 
	 * @param args
	 *            the console parameters given to the program
	 */
	public static void main(String[] args) {
		printHeader();
		
		List<String> arguments = Arrays.asList(args);
		if(arguments.contains(ApiCommands.GRADE_DISTRIBUTION.getConsoleCommand()) || arguments.contains(ApiCommands.GRADE_DISTRIBUTION.getConsoleShortCommand())) {
			getGradeDistribution();			
		} else  if(arguments.contains(ApiCommands.SCANNER_STATES.getConsoleCommand()) || arguments.contains(ApiCommands.SCANNER_STATES.getConsoleShortCommand())) {
			getScannerStates();
		} else if(arguments.contains(ApiCommands.RECENT_SCANS.getConsoleCommand()) || arguments.contains(ApiCommands.RECENT_SCANS.getConsoleShortCommand())) {
			getRecentScans(args);
		} else if(arguments.contains(ApiCommands.RETRIEVE_ASSESSMENT.getConsoleCommand()) || arguments.contains(ApiCommands.RETRIEVE_ASSESSMENT.getConsoleShortCommand())) {
			if(arguments.contains("-host")) {
				retrieveAssessment(args);
			} else {
				printUsage();
			}
		} else if(arguments.contains(ApiCommands.RETRIEVE_TEST_RESULT.getConsoleCommand()) || arguments.contains(ApiCommands.RETRIEVE_TEST_RESULT.getConsoleShortCommand())) {
			if(arguments.contains("-id")) {
				retrieveResults(args);
			} else {
				printUsage();
			}
		} else {
			printUsage();
		}
	}

	/**
	 * Parse the arguments for the existence of a proxy argument. If availbale
	 * set the proxy and remove parameter from args.
	 * 
	 */
	private void configureProxy() {
		File file = new File("proxy");
		if (!file.exists()) {
			return;
		}

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String proxy = reader.readLine();
			if (proxy != null) {
				this.proxy = proxy;
			}
			reader.close();
		} catch (FileNotFoundException e) {
			Logger.getGlobal().severe(e.getLocalizedMessage());
		} catch (IOException e) {
			Logger.getGlobal().severe(e.getLocalizedMessage());
		}
	}

	/**
	 * Print out an assessment. May be cached or fresh one.
	 * 
	 * @param args
	 *            - the console parameters
	 */
	private static void retrieveAssessment(String[] args) {
		String host = ConsoleUtilities.arrayValueMatchRegex(args, "-host=(.+)");

		Boolean hidden = false;
		List<String> arguments = Arrays.asList(args);
		if (arguments.contains("-hidden")) {
			hidden = true;
		}

		Api obervatoryApi = new Api();

		JSONObject scan = null;
		if (arguments.contains("-rescan")) {
			obervatoryApi.retrieveAssessment(host, hidden);
			// Loop while observatory is working. Ask every 10 seconds
			boolean running = true;
			while (running) {
				scan = obervatoryApi.retrieveCachedAssessment(host);
				try {
					if (scan.getString("state").equalsIgnoreCase("FINISHED")) {
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
	 * 
	 * @param args
	 *            - the console parameters
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
	 * 
	 * @param args
	 *            - the console parameters
	 */
	private static void getRecentScans(String[] args) {
		String max = null;
		String min = null;

		for (String arg : args) {
			if (arg.contains("-max")) {
				max = ConsoleUtilities.arrayValueMatchRegex(args, "-max=(.+)");
			} else if (arg.contains("-min")) {
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
		System.out.println(ApiCommands.RETRIEVE_ASSESSMENT.getConsoleShortCommand() + ", "
				+ ApiCommands.RETRIEVE_ASSESSMENT.getConsoleCommand());
		System.out.println("	Mandantory parameter:");
		System.out.println("	-host (String) - hostname");
		System.out.println("	Additional parameter:");
		System.out.println("	-rescan (boolean) - start fresh scan; default false");
		System.out.println("	-hidden (boolean) - results not shown in getRecentScans; default false");
		System.out.println("");
		System.out.println(ApiCommands.RETRIEVE_TEST_RESULT.getConsoleShortCommand() + ", "
				+ ApiCommands.RETRIEVE_TEST_RESULT.getConsoleCommand());
		System.out.println("	Mandantory parameter:");
		System.out.println("	-id - scan id");
		System.out.println("");
		System.out.println(
				"If you need to use a proxy, please create file proxy in this directory and fill with one line containing proxy ip:port");
	}
}
