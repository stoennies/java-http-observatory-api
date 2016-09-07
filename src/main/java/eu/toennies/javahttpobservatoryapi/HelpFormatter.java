package eu.toennies.javahttpobservatoryapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import eu.toennies.javahttpobservatoryapi.commands.ApiCommand;
import eu.toennies.javahttpobservatoryapi.commands.ApiCommands;
import eu.toennies.javahttpobservatoryapi.commands.CommandArgument;

/**
 * This class is a utility class for printing the help message. It is based on
 * the HelpFormatter class from the Apache CI project.
 * 
 * @author Sascha Tönnies <https://github.com/stoennies>
 *
 */
public class HelpFormatter {

	// --------------------------------------------------------------- Constants

	/** default number of characters per line */
	private static final int DEFAULT_WIDTH = 105;

	/** default padding to the left of each line */
	private static final int DEFAULT_LEFT_PAD = 1;

	/** number of space characters to be prefixed to each description line */
	private static final int DEFAULT_DESC_PAD = 3;

	/** the string to display at the beginning of the usage statement */
	public static final String DEFAULT_SYNTAX_PREFIX = "usage: ";

	/**
	 * the new line string
	 */
	private String newLine = System.getProperty("line.separator");

	/**
	 * number of characters per line
	 * 
	 */
	private int width = DEFAULT_WIDTH;

	/**
	 * amount of padding to the left of each line
	 */
	private int leftPad = DEFAULT_LEFT_PAD;

	/**
	 * the number of characters of padding to be prefixed to each description
	 * line
	 */
	private int descPad = DEFAULT_DESC_PAD;

	/**
	 * the string to display at the beginning of the usage statement
	 */
	private String syntaxPrefix = DEFAULT_SYNTAX_PREFIX;

	private PrintWriter pw;

	public HelpFormatter(PrintWriter pw) {
		this.pw = pw;
	}

	/**
	 * @return the defaultNewLine
	 */
	public String getNewLine() {
		return newLine;
	}

	/**
	 * @param defaultNewLine
	 *            the defaultNewLine to set
	 */
	public void setNewLine(String defaultNewLine) {
		this.newLine = defaultNewLine;
	}

	/**
	 * @return the syntaxPrefix
	 */
	public String getSyntaxPrefix() {
		return syntaxPrefix;
	}

	/**
	 * @param syntaxPrefix
	 *            the syntaxPrefix to set
	 */
	public void setSyntaxPrefix(String syntaxPrefix) {
		this.syntaxPrefix = syntaxPrefix;
	}

	/**
	 * @return the leftPad
	 */
	public int getLeftPadding() {
		return leftPad;
	}

	/**
	 * @return the defaultDescPad
	 */
	public int getDescPadding() {
		return descPad;
	}

	/**
	 * @param leftPad
	 *            the leftPad to set
	 */
	public void setLeftPadding(int leftPad) {
		this.leftPad = leftPad;
	}

	/**
	 * @param defaultDescPad
	 *            the defaultDescPad to set
	 */
	public void setDescPadding(int defaultDescPad) {
		this.descPad = defaultDescPad;
	}

	/**
	 * Returns the 'width'.
	 * 
	 * @return the 'width'
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * @param width
	 *            the defaultWidth to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Print the help for <code>ApiCommands</code> with the specified command
	 * line syntax. This method prints help information to System.out.
	 *
	 * @param commands
	 *            ApiCommands to format
	 */
	public void printHelp(ApiCommands[] commands) {
		String jarName = "java-http-observatory-api-" + Api.getVersion() + ".jar";
		String jarExecution = "java -jar " + jarName;
		String footer = "If you need to use a proxy, please create a file called \"proxy\" in program directory and fill with one line containing proxy ip:port";

		printHelp(getWidth(), jarExecution, getHeader(), commands, footer);
	}

	/**
	 * Print the help for <code>ApiCommands[]</code> with the specified command
	 * line syntax. This method prints help information to System.out.
	 *
	 * @param width
	 *            the number of characters to be displayed on each line
	 * @param cmdLineSyntax
	 *            the syntax for this application
	 * @param header
	 *            the banner to display at the beginning of the help
	 * @param commands
	 *            commands the Options instance
	 * @param footer
	 *            the banner to display at the end of the help
	 */
	public void printHelp(int width, String cmdLineSyntax, String header, ApiCommands[] commands, String footer) {
		printHelp(width, cmdLineSyntax, header, commands, getLeftPadding(), getDescPadding(), footer);
		pw.flush();
	}

	/**
	 * Print the help for <code>ApiCommands[]</code> with the specified command
	 * line syntax.
	 *
	 * @param width
	 *            the number of characters to be displayed on each line
	 * @param cmdLineSyntax
	 *            the syntax for this application
	 * @param header
	 *            the banner to display at the beginning of the help
	 * @param commands
	 *            the Commands instance
	 * @param leftPad
	 *            the number of characters of padding to be prefixed to each
	 *            line
	 * @param descPad
	 *            the number of characters of padding to be prefixed to each
	 *            description line
	 * @param footer
	 *            the banner to display at the end of the help
	 *
	 * @throws IllegalStateException
	 *             if there is no room to print a line
	 */
	public void printHelp(int width, String cmdLineSyntax, String header, ApiCommands[] commands, int leftPad,
			int descPad, String footer) {
		if (cmdLineSyntax == null || cmdLineSyntax.length() == 0) {
			throw new IllegalArgumentException("cmdLineSyntax not provided");
		}

		if (header != null && header.trim().length() > 0) {
			pw.print(header);
		}

		printUsage(width, cmdLineSyntax);

		printOptions(width, commands, leftPad, descPad);

		if (footer != null && footer.trim().length() > 0) {
			pw.print("\n");
			printWrapped(width, footer);
		}
	}

	/**
	 * Print the cmdLineSyntax to the specified writer, using the specified
	 * width.
	 *
	 * @param width
	 *            The number of characters per line for the usage statement.
	 * @param cmdLineSyntax
	 *            The usage statement.
	 */
	public void printUsage(int width, String cmdLineSyntax) {
		int argPos = cmdLineSyntax.indexOf(' ') + 1;

		printWrapped(pw, width, getSyntaxPrefix().length() + argPos, getSyntaxPrefix() + cmdLineSyntax);
	}

	/**
	 * Print the specified text to the specified PrintWriter.
	 *
	 * @param width
	 *            The number of characters to display per line
	 * @param nextLineTabStop
	 *            The position on the next line for the first tab.
	 * @param text
	 *            The text to be written to the PrintWriter
	 */
	public void printWrapped(PrintWriter pw, int width, int nextLineTabStop, String text) {
		StringBuffer sb = new StringBuffer(text.length());

		renderWrappedTextBlock(sb, width, nextLineTabStop, text);
		pw.println(sb.toString());
	}

	/**
	 * Render the specified text width a maximum width. This method differs from
	 * renderWrappedText by not removing leading spaces after a new line.
	 *
	 * @param sb
	 *            The StringBuffer to place the rendered text into.
	 * @param width
	 *            The number of characters to display per line
	 * @param nextLineTabStop
	 *            The position on the next line for the first tab.
	 * @param text
	 *            The text to be rendered.
	 */
	private Appendable renderWrappedTextBlock(StringBuffer sb, int width, int nextLineTabStop, String text) {
		try {
			BufferedReader in = new BufferedReader(new StringReader(text));
			String line;
			boolean firstLine = true;
			while ((line = in.readLine()) != null) {
				if (!firstLine) {
					sb.append(getNewLine());
				} else {
					firstLine = false;
				}
				renderWrappedText(sb, width, nextLineTabStop, line);
			}
		} catch (IOException e) // NOPMD
		{
			// cannot happen
		}

		return sb;
	}

	/**
	 * Render the specified text and return the rendered Options in a
	 * StringBuffer.
	 *
	 * @param sb
	 *            The StringBuffer to place the rendered text into.
	 * @param width
	 *            The number of characters to display per line
	 * @param nextLineTabStop
	 *            The position on the next line for the first tab.
	 * @param text
	 *            The text to be rendered.
	 *
	 * @return the StringBuffer with the rendered Options contents.
	 */
	protected StringBuffer renderWrappedText(StringBuffer sb, int width, int nextLineTabStop, String text) {
		int pos = findWrapPos(text, width, 0);

		if (pos == -1) {
			sb.append(rtrim(text));

			return sb;
		}
		sb.append(rtrim(text.substring(0, pos))).append(getNewLine());

		if (nextLineTabStop >= width) {
			// stops infinite loop happening
			nextLineTabStop = 1;
		}

		// all following lines must be padded with nextLineTabStop space
		// characters
		final String padding = createPadding(nextLineTabStop);

		while (true) {
			text = padding + text.substring(pos).trim();
			pos = findWrapPos(text, width, 0);

			if (pos == -1) {
				sb.append(text);

				return sb;
			}

			if (text.length() > width && pos == nextLineTabStop - 1) {
				pos = width;
			}

			sb.append(rtrim(text.substring(0, pos))).append(getNewLine());
		}
	}

	/**
	 * Finds the next text wrap position after <code>startPos</code> for the
	 * text in <code>text</code> with the column width <code>width</code>. The
	 * wrap point is the last position before startPos+width having a whitespace
	 * character (space, \n, \r). If there is no whitespace character before
	 * startPos+width, it will return startPos+width.
	 *
	 * @param text
	 *            The text being searched for the wrap position
	 * @param width
	 *            width of the wrapped text
	 * @param startPos
	 *            position from which to start the lookup whitespace character
	 * @return position on which the text must be wrapped or -1 if the wrap
	 *         position is at the end of the text
	 */
	protected int findWrapPos(String text, int width, int startPos) {
		// the line ends before the max wrap pos or a new line char found
		int pos = text.indexOf('\n', startPos);
		if (pos != -1 && pos <= width) {
			return pos + 1;
		}

		pos = text.indexOf('\t', startPos);
		if (pos != -1 && pos <= width) {
			return pos + 1;
		}

		if (startPos + width >= text.length()) {
			return -1;
		}

		// look for the last whitespace character before startPos+width
		for (pos = startPos + width; pos >= startPos; --pos) {
			final char c = text.charAt(pos);
			if (c == ' ' || c == '\n' || c == '\r') {
				break;
			}
		}

		// if we found it - just return
		if (pos > startPos) {
			return pos;
		}

		// if we didn't find one, simply chop at startPos+width
		pos = startPos + width;

		return pos == text.length() ? -1 : pos;
	}

	/**
	 * Remove the trailing whitespace from the specified String.
	 *
	 * @param s
	 *            The String to remove the trailing padding from.
	 *
	 * @return The String of without the trailing padding
	 */
	protected String rtrim(String s) {
		if (s == null || s.length() == 0) {
			return s;
		}

		int pos = s.length();

		while (pos > 0 && Character.isWhitespace(s.charAt(pos - 1))) {
			--pos;
		}

		return s.substring(0, pos);
	}

	/**
	 * Return a String of padding of length <code>len</code>.
	 *
	 * @param len
	 *            The length of the String of padding to create.
	 *
	 * @return The String of padding
	 */
	protected String createPadding(int len) {
		char[] padding = new char[len];
		Arrays.fill(padding, ' ');

		return new String(padding);
	}

	/**
	 * Print the specified text to the specified PrintWriter.
	 *
	 * @param width
	 *            The number of characters to display per line
	 * @param text
	 *            The text to be written to the PrintWriter
	 */
	public void printWrapped(int width, String text) {
		printWrapped(pw, width, 0, text);
	}

	/**
	 * Print the header.
	 */
	public String getHeader() {
		StringBuffer sb = new StringBuffer();
		sb.append("\n");
		sb.append(
				"     _                   ____  _                              _                            _____ _____ ");
		sb.append("\n");
		sb.append(
				"    | |                 / __ \\| |                            | |                     /\\   |  __ \\_   _|");
		sb.append("\n");
		sb.append(
				"    | | __ ___   ____ _| |  | | |__  ___  ___ _ ____   ____ _| |_ ___  _ __ _   _   /  \\  | |__) || |  ");
		sb.append("\n");
		sb.append(
				"_   | |/ _` \\ \\ / / _` | |  | | '_ \\/ __|/ _ \\ '__\\ \\ / / _` | __/ _ \\| '__| | | | / /\\ \\ |  ___/ | |  ");
		sb.append("\n");
		sb.append(
				" |__| | (_| |\\ V / (_| | |__| | |_) \\__ \\  __/ |   \\ V / (_| | || (_) | |  | |_| |/ ____ \\| |    _| |_ ");
		sb.append("\n");
		sb.append(
				"\\____/ \\__,_| \\_/ \\__,_|\\____/|_.__/|___/\\___|_|    \\_/ \\__,_|\\__\\___/|_|   \\__, /_/    \\_\\_|   |_____|");
		sb.append("\n");
		sb.append("by Sascha Toennies  <https://github.com/stoennies>");
		sb.append("\n");
		sb.append("and contributors (https://github.com/stoennies/java-http-observatory-api/graphs/contributors)");
		sb.append("\n");
		sb.append(
				"-------------------------------------------------------------------------------------------------------");
		sb.append("\n");

		return sb.toString();
	}

	/**
	 * Print the help for the specified Options to the specified writer, using
	 * the specified width, left padding and description padding.
	 *
	 * @param width
	 *            The number of characters to display per line
	 * @param commands
	 *            commands
	 * @param leftPad
	 *            the number of characters of padding to be prefixed to each
	 *            line
	 * @param descPad
	 *            the number of characters of padding to be prefixed to each
	 *            description line
	 */
	public void printOptions(int width, ApiCommands[] commands, int leftPad, int descPad) {
		StringBuffer sb = new StringBuffer();

		renderCommands(sb, width, commands, leftPad, descPad);
		pw.println(sb.toString());
	}

	/**
	 * Render the specified ApiCommands and return the rendered ApiCommand in a
	 * StringBuffer.
	 *
	 * @param sb
	 *            The StringBuffer to place the rendered ApiCommand into.
	 * @param width
	 *            The number of characters to display per line
	 * @param commands
	 *            The command line ApiCommands
	 * @param leftPad
	 *            the number of characters of padding to be prefixed to each
	 *            line
	 * @param descPad
	 *            the number of characters of padding to be prefixed to each
	 *            description line
	 *
	 * @return the StringBuffer with the rendered Options contents.
	 */
	protected StringBuffer renderCommands(StringBuffer sb, int width, ApiCommands[] commands, int leftPad,
			int descPad) {
		final String lpad = createPadding(leftPad);
		final String dpad = createPadding(descPad);
		int max = 0;

		List<StringBuffer> prefixList = new ArrayList<StringBuffer>();
		for (ApiCommands cmds : commands) {
			ApiCommand command = cmds.getCommand();
			StringBuffer optBuf = new StringBuffer();

			optBuf.append(lpad).append(command.getConsoleShortCommand());
			optBuf.append(", ").append(command.getConsoleCommand());

			if (command.hasArgs()) {
				renderArgs(optBuf, command);
			}

			prefixList.add(optBuf);
			max = optBuf.length() > max ? optBuf.length() : max;
		}

		int x = 0;

		for (Iterator<ApiCommands> it = Arrays.asList(commands).iterator(); it.hasNext();) {
			ApiCommands option = it.next();
			StringBuilder opt2Buf = new StringBuilder(prefixList.get(x++).toString());

			if (opt2Buf.length() < max) {
				opt2Buf.append(createPadding(max - opt2Buf.length()));
			}

			opt2Buf.append(dpad);

			int nextLineTabStop = max + descPad;

			if (option.getCommand().getDescription() != null) {
				opt2Buf.append(option.getCommand().getDescription());
			}

			renderWrappedText(sb, width, nextLineTabStop, opt2Buf.toString());

			if (it.hasNext()) {
				sb.append(getNewLine());
			}
		}

		return sb;
	}

	private void renderArgs(StringBuffer optBuf, final ApiCommand command) {
		for (CommandArgument arg : command.getCommandArguments()) {
			String argName = arg.getKey();
			if (argName != null && argName.length() == 0) {
				// if the option has a blank argname
				optBuf.append(' ');
			} else {
				optBuf.append(" ");
				optBuf.append("<").append(argName).append("=value>");
			}
		}		
	}

}
