package com.orangebank.codechallenge.util;

import java.util.Arrays;

import com.orangebank.codechallenge.exception.TransactionStatusEnumNotFoundException;

public enum TransactionEnum {

	PENDING, SETTLED, FUTURE, INVALID;

	/**
	 * @return the Enum representation for the given string.
	 * @throws IllegalArgumentException if unknown string.
	 */
	public static TransactionEnum fromString(String s) {
		return Arrays.stream(TransactionEnum.values()).filter(v -> v.name().equalsIgnoreCase(s)).findFirst()
				.orElseThrow(() -> new TransactionStatusEnumNotFoundException("Not Enum with value: " + s));
	}
}
