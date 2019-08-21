package com.orangebank.codechallenge.util;

import java.util.Arrays;

import com.orangebank.codechallenge.exception.ChannelStatusEnumNotFoundException;

public enum ChannelStatusEnum {

	CLIENT, ATM, INTERNAL;

	/**
	 * @return the Enum representation for the given string.
	 * @throws IllegalArgumentException if unknown string.
	 */
	public static ChannelStatusEnum fromString(String s) {
		return Arrays.stream(ChannelStatusEnum.values()).filter(v -> v.name().equalsIgnoreCase(s)).findFirst()
				.orElseThrow(() -> new ChannelStatusEnumNotFoundException("unknown value: " + s));
	}
}
