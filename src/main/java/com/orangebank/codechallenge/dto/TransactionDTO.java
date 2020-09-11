package com.orangebank.codechallenge.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonView;
import com.orangebank.codechallenge.domain.View;
import com.orangebank.codechallenge.util.ChannelStatusEnum;
import com.orangebank.codechallenge.util.TransactionEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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
 * @return TransactionEnum
 */

/** 
 * @return ChannelStatusEnum
 */
@Getter
@Setter
@NoArgsConstructor
@JsonView(View.Summary.class)
public class TransactionDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8797238103309804952L;
	private String reference;
	private String accountIban;
	private Date date;
	private Double amount;
	private Double fee;
	private String description;
	private TransactionEnum status;
	private ChannelStatusEnum channel;

}
