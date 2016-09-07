package eu.toennies.javahttpobservatoryapi.commands;

/**
 * Enumaration for available API commands.
 * 
 * @author Sascha Tönnies <https://github.com/stoennies>
 *
 */
public enum ApiCommands {
	GRADE_DISTRIBUTION(new GradeDistributionCommand()),
	SCANNER_STATES(new ScannerStatesCommand()),
	RECENT_SCANS(new RecentScansCommand()),
	INVOKE_ASSESSMENT(new InvokeAssessmentCommand()),
	RETRIEVE_ASSESSMENT(new RetrieveAssessmentCommand()),
	RETRIEVE_TEST_RESULT(new RetrieveTestResultsCommand()),
	HELP(new HelpCommand());


	private ApiCommand apiCommand;

	/**
	 * Constructor for new API command.
	 * 
	 * @param apiCommand - the original API command as listed in the API documentation of Mozilla's HTTP Observatory
	 * @param console - the command for the console
	 * @param consoleShort - the short version of the console command
	 */
	ApiCommands(final ApiCommand command) {
		this.apiCommand = command;
	}
	
	public ApiCommand getCommand() {
		return apiCommand;
	}

}
