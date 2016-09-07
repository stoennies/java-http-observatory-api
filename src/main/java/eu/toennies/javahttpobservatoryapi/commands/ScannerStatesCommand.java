package eu.toennies.javahttpobservatoryapi.commands;

/**
 * This returns the state of the scanner. It can be useful for determining how
 * busy the HTTP Observatory is.
 * 
 * Example of a scanner state object
 * 
 *         e.g. { "ABORTED": 10, "FAILED": 281, "FINISHED": 46240, "PENDING":
 *         122, "STARTING": 96, "RUNNING: 128, }
 * 
 * @author Sascha Tönnies <https://github.com/stoennies>
 *
 */
public class ScannerStatesCommand extends GetCommandWithoutParameter {

	public ScannerStatesCommand() {
		super("getScannerStates", "scannerStates", "s", "Scanner states", "Retrieve scanner states");
	}
}