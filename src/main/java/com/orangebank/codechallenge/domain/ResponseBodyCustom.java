package com.orangebank.codechallenge.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import com.orangebank.codechallenge.dto.TransactionDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/** 
 * @return LocalDateTime
 */

/** 
 * @return HttpStatus
 */

/** 
 * @return String
 */

/** 
 * @return List<TransactionDTO>
 */
@Getter
@Setter
@NoArgsConstructor
public class ResponseBodyCustom {

	@JsonView(View.Summary.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
	private LocalDateTime timestamp;

	@JsonView(View.Summary.class)
	private HttpStatus status;

	@JsonView(View.Summary.class)
	private String message;

	@JsonView(View.Summary.class)
	private List<TransactionDTO> transactions;

}
