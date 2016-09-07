package eu.toennies.javahttpobservatoryapi.commands;

import java.util.List;

import org.json.JSONObject;

public class GetCommandWithoutParameter extends ApiCommand {

	public GetCommandWithoutParameter(String apiCommand, String console, String consoleShort, String header, String description) {
		super(apiCommand, console, consoleShort, header, description);
	}

	@Override
	public JSONObject run(List<String> arguments) {
		return super.callApiCommand();
	}

}
