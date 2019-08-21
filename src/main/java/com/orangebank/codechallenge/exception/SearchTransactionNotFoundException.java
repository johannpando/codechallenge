package com.orangebank.codechallenge.exception;

import lombok.Getter;

@Getter
public class SearchTransactionNotFoundException extends RuntimeException {

	private String reference;

	private String status;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3733502894509250258L;

	public SearchTransactionNotFoundException(String message) {
		super(message);
	}

	public SearchTransactionNotFoundException(String reference, String status) {
		this.reference = reference;
		this.status = status;
	}
}
