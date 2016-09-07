package eu.toennies.javahttpobservatoryapi.commands;

import java.io.PrintWriter;
import java.util.List;

import org.json.JSONObject;

import eu.toennies.javahttpobservatoryapi.HelpFormatter;

public class HelpCommand extends ApiCommand {

	public HelpCommand() {
		super(null, "help", "h", "Help", "Print out usage information.");
	}

	@Override
	public JSONObject run(List<String> arguments) {
		HelpFormatter hf = new HelpFormatter(new PrintWriter(System.out));
		hf.printHelp(ApiCommands.values());
		return new JSONObject();
	}

}
