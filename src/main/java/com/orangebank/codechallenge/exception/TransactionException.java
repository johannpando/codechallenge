package com.orangebank.codechallenge.exception;

public class TransactionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2226383405706292010L;

	public TransactionException(String message) {
		super(message);
	}
}
