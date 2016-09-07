package eu.toennies.javahttpobservatoryapi;

import java.io.IOException;

public class ApiException extends IOException {

	private static final long serialVersionUID = -1853339371981068835L;

	public ApiException() {
		super();
	}

	public ApiException(String message) {
		super(message);
	}

	public ApiException(Throwable cause) {
		super(cause);
	}

	public ApiException(String message, Throwable cause) {
		super(message, cause);
	}

}
