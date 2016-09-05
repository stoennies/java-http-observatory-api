package eu.toennies.javahttpobservatoryapi;

/**
 * Enumaration for available API commands.
 * 
 * @author Sascha Tönnies <https://github.com/stoennies>
 *
 */
public enum ApiCommands {
	GRADE_DISTRIBUTION("getGradeDistribution", "--gradeDistribution", "-g"), 
	SCANNER_STATES("getScannerStates", "--scannerStates", "-s"),
	RECENT_SCANS("getRecentScans", "--recentScans", "-r"),
	RETRIEVE_ASSESSMENT("analyze", "--retrieveAssessment", "-a"),
	RETRIEVE_TEST_RESULT("getScanResults", "--retrieveTestResult", "-t");

	private final String apiCommand;
	private String consoleCommand;
	private String consoleShortCommand;

	/**
	 * Constructor for new API command.
	 * 
	 * @param apiCommand - the original API command as listed in the API documentation of Mozilla's HTTP Observatory
	 * @param console - the command for the console
	 * @param consoleShort - the short version of the console command
	 */
	ApiCommands(final String apiCommand, final String console, final String consoleShort) {
		this.apiCommand = apiCommand;
		this.consoleCommand = console;
		this.consoleShortCommand = consoleShort;
		
	}
	
	/**
	 * Retrieve the API command.
	 * 
	 * @return the API command to send to the API endpoint
	 */
	public String getApiCommand() {
		return this.apiCommand;
	}

	/**
	 * The long command version used as program parameter.
	 * @return the command
	 */
	public String getConsoleCommand() {
		return consoleCommand;
	}

	/**
	 * The short version used as program parameter
	 * @return the short command version
	 */
	public String getConsoleShortCommand() {
		return consoleShortCommand;
	}

}
