package com.orangebank.codechallenge.exception;

public class TransactionNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9105452740825267844L;

	public TransactionNotFoundException(String message) {
		super(message);
	}
}
