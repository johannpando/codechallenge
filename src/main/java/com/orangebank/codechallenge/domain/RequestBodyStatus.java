package com.orangebank.codechallenge.domain;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestBodyStatus {

	@NotEmpty(message = "Reference is mandatory")
	private String reference;

	private String channel;

}
