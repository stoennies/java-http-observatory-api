package eu.toennies.javahttpobservatoryapi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import eu.toennies.javahttpobservatoryapi.commands.ApiCommand;
import eu.toennies.javahttpobservatoryapi.commands.ApiCommands;

/**
 * The console class. Starting point for the programm.
 * 
 * @author Sascha Tönnies <https://github.com/stoennies>
 *
 */
public class Console {

	/**
	 * Proxy settings
	 */
	private String proxy = null;


	/**
	 * Holder class for singelton according to the Initialization-on-demand
	 * holder idiom.
	 * 
	 * @author Sascha Tönnies <https://github.com/stoennies>
	 *
	 */
	private static class Holder {
		private static final Console INSTANCE = new Console();
	}

	/**
	 * Retrieve the proxy
	 * 
	 * @return a proxyIP:port if configured null otherwise
	 */
	public String getProxy() {
		return proxy;
	}

	/**
	 * Hidden constructor for singelton.
	 */
	private Console() {
		configureProxy();
	}

	/**
	 * Get the console.
	 * 
	 * @return the singelton console
	 */
	public static Console getInstance() {
		return Holder.INSTANCE;
	}

	/**
	 * The main class.
	 * 
	 * @param args
	 *            the console parameters given to the program
	 */
	public static void main(String[] args) {
		List<String> arguments = Arrays.asList(args);
		
		if(arguments.contains(ApiCommand.DEFAULT_CMD_PREFIX + "h") || arguments.contains(ApiCommand.DEFAULT_LONG_CMD_PREFIX + "help")) {
			printHelp();
			return;
		}
		
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"));
			for (ApiCommands cmds : ApiCommands.values()) {
				ApiCommand cmd = cmds.getCommand();
				if (cmd.shouldStart(arguments)) {
					try {
						JSONObject json = cmd.run(arguments);

						pw.println(cmd.getHeader());
						pw.println("");
						pw.println(ConsoleUtilities.mapToConsoleOutput(ConsoleUtilities.jsonToMap(json)));
						pw.flush();

					} catch (JSONException e) {
						System.err.println("Could not pars API response: " + e.getLocalizedMessage());
					} catch (IllegalArgumentException ia) {
						System.err.println(ia.getLocalizedMessage());
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			System.err.println("Could not write to System.out using UTF-8 encoding.");
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	private static void printHelp() {
		try {
			HelpFormatter hf = new HelpFormatter(new PrintWriter(new OutputStreamWriter(System.out, "UTF-8")));
			hf.printHelp(ApiCommands.values());
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
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
}
