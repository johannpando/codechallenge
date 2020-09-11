package com.orangebank.codechallenge.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "transactions")

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
public class TransactionEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4815618495105140250L;

	@Id
	private String reference;

	@NotEmpty(message = "account_iban is mandatory")
	private String accountIban;

	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	@NotNull
	private Double amount;
	private Double fee;
	private String description;
	private String status;
	private String channel;

}
