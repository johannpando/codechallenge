package com.orangebank.codechallenge.exception;

public class AccountBalanceBellowZeroNotAllowedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9020178605509275992L;

	public AccountBalanceBellowZeroNotAllowedException(String message) {
		super(message);
	}
}
