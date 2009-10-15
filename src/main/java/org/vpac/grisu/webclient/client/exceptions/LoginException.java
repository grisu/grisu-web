package org.vpac.grisu.webclient.client.exceptions;

import java.io.Serializable;

public class LoginException extends Exception implements Serializable {
	
	String errorMessage;
	
	public LoginException() {
	}
	

	public LoginException(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public String getLoginError() {
		return errorMessage;
	}

}
