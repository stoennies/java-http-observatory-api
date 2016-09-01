package eu.toennies.javahttpobservatoryapi;

public enum ApiCommands {
	GRADE_DISTRIBUTION("getGradeDistribution", "--gradeDistribution", "-g"), 
	SCANNER_STATES("getScannerStates", "--scannerStates", "-s"),
	RECENT_SCANS("getRecentScans", "--recentScans", "-r"),
	RETRIEVE_ASSESSMENT("analyze", "--retrieveAssessment", "-a"),
	RETRIEVE_TEST_RESULT("getScanResults", "--retrieveTestResult", "-t");

	private final String apiCommand;
	private String consoleCommand;
	private String consoleShortCommand;

	ApiCommands(final String apiCommand, final String console, final String consoleShort) {
		this.apiCommand = apiCommand;
		this.consoleCommand = console;
		this.consoleShortCommand = consoleShort;
		
	}
	
	public String apiCommand() {
		return this.apiCommand;
	}

	public String consoleCommand() {
		return consoleCommand;
	}

	public String consoleShortCommand() {
		return consoleShortCommand;
	}

}
