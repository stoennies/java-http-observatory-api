package eu.toennies.javahttpobservatoryapi.commands;

public class CommandArgument {

	private String key;
	private String description;
	private boolean mandatory;

	public CommandArgument(final String key, final String description, final boolean mandatory) throws IllegalArgumentException {
		this.key = key;
		this.description = description;
		this.mandatory = mandatory;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the mandatory
	 */
	public boolean isMandatory() {
		return mandatory;
	}

}
