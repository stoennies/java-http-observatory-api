package eu.toennies.javahttpobservatoryapi.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import eu.toennies.javahttpobservatoryapi.Api;

public abstract class ApiCommand {

    public static final String DEFAULT_CMD_PREFIX = "-";
    public static final String DEFAULT_LONG_CMD_PREFIX = "--";
    
	private final String apiCommand;
	private String consoleCommand;
	private String consoleShortCommand;
	private String header;
	private String description;
	private List<CommandArgument> commandArguments = new ArrayList<CommandArgument>();

	/**
	 * Constructor for new API command.
	 * 
	 * @param apiCommand
	 *            - the original API command as listed in the API documentation
	 *            of Mozilla's HTTP Observatory
	 * @param console
	 *            - the command for the console
	 * @param consoleShort
	 *            - the short version of the console command
	 */
	public ApiCommand(final String apiCommand, final String console, final String consoleShort, final String header, final String description) {
		this.apiCommand = apiCommand;
		this.consoleCommand = console;
		this.consoleShortCommand = consoleShort;
		this.description = description;
		this.header = header;
	}
	
	/**
	 * @param arguments
	 * @return the api response as a map
	 */
	public abstract JSONObject run(List<String> arguments) throws IllegalArgumentException;
	
	
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
	 * 
	 * @return the command
	 */
	public String getConsoleCommand() {
		return  ApiCommand.DEFAULT_LONG_CMD_PREFIX + consoleCommand;
	}

	/**
	 * The short version used as program parameter
	 * 
	 * @return the short command version
	 */
	public String getConsoleShortCommand() {
		return ApiCommand.DEFAULT_CMD_PREFIX + consoleShortCommand;
	}

	public JSONObject callApiCommand() {
		return callApiCommand(null);
	}

	/**
	 * Retrieve the description of the command.
	 * @return the command description
	 */
	public String getDescription() {
		return this.description;
	}
	
	public String getHeader() {
		return this.header;
	}
	
	public JSONObject callApiCommand(Map<String, String> parameters) {
		JSONObject apiInfo = null;
		try {
			Api api = new Api();
			apiInfo = new JSONObject(api.sendApiGetRequest(getApiCommand(), parameters));
		} catch (JSONException e) {
			Logger.getGlobal().severe("Could not build result: " + e.getLocalizedMessage());
		} catch (IOException e) {
			Logger.getGlobal().severe("Could not send API request: " + e.getLocalizedMessage());
		}
		return apiInfo;
	}

	public String getHelp() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getConsoleShortCommand());
		buffer.append(", ");
		buffer.append(getConsoleCommand());
		buffer.append("    ");
		buffer.append(getDescription());
		
		return buffer.toString();
	}

	/**
	 * Retrieves the possible command parameters.
	 * 
	 * @return the commandParameters
	 */
	public List<CommandArgument> getCommandArguments() {
		return commandArguments;
	}
	
	/**
	 * Adds an argument to the command.
	 * 
	 * @param param - the command argument to add
	 */
	public void addCommandArgument(CommandArgument param) {
		this.commandArguments.add(param);
	}

	/**
	 * Does this command has arguments?
	 * @return true if the command has arguments
	 */
	public boolean hasArgs() {
		return(commandArguments.isEmpty() ? false : true); 
	}
	
	public boolean shouldStart(List<String> arguments) {
		return arguments.contains(getConsoleCommand()) || arguments.contains(getConsoleShortCommand());
	}
}
