package com.orangebank.codechallenge.domain;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/** 
 * @return String
 */

/** 
 * @return String
 */

/** 
 * @return Date
 */

/** 
 * @return Double
 */

/** 
 * @return Double
 */

/** 
 * @return String
 */

/** 
 * @return String
 */

/** 
 * @return String
 */
@Getter
@Setter
@NoArgsConstructor

/** 
 * @return String
 */
@ToString
public class RequestBodyCustom {

	private String reference;

	@JsonProperty("account_iban")
	@NotEmpty(message = "account_iban is mandatory")
	private String accountIban;

	private Date date;

	@NotNull(message = "amount is mandatory")
	private Double amount;

	private Double fee;

	private String description;

	private String channel;

	private String status;
}
