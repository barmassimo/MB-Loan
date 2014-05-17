package it.massimobarbieri.mbloan.exception;

public class ApplicationLogicException extends Exception {

	private static final long serialVersionUID = 1L;

	public ApplicationLogicException(String message) {
		super(message);
	}

	public ApplicationLogicException(String message, Throwable throwable) {
		super(message, throwable);
	}

}