package com.orangebank.codechallenge.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.orangebank.codechallenge.domain.RequestBodyCustom;
import com.orangebank.codechallenge.domain.ResponseBodyCustom;
import com.orangebank.codechallenge.domain.View;
import com.orangebank.codechallenge.dto.TransactionDTO;
import com.orangebank.codechallenge.service.TransactionCreateService;
import com.orangebank.codechallenge.service.TransactionSearchService;
import com.orangebank.codechallenge.util.ChannelStatusEnum;
import com.orangebank.codechallenge.util.TransactionEnum;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(path = "api", produces = { MediaType.APPLICATION_JSON_VALUE })
@Validated
@Api(value = "This is the controller for the application")
public class TransactionController {

	private TransactionCreateService transactionCreateService;

	private TransactionSearchService transactionSearchService;

	
	/** 
	 * @param request
	 * @return ResponseEntity<ResponseBodyCustom>
	 */
	@JsonView(View.Summary.class)
	@PostMapping("/transactions")
	@ApiOperation(value = "Create a transaction", notes = "If any error occurs, it will be intercepted by an error handler")
	public ResponseEntity<ResponseBodyCustom> createTransaction(@RequestBody @Valid RequestBodyCustom request) {
		TransactionDTO transactionDTO = requestToDto(request);
		TransactionDTO t = transactionCreateService.createTransaction(transactionDTO);
		ResponseBodyCustom response = new ResponseBodyCustom();
		response.setMessage("Transaction created");
		response.setStatus(HttpStatus.OK);
		response.setTimestamp(LocalDateTime.now());
		List<TransactionDTO> transactions = new ArrayList<>();
		transactions.add(t);
		response.setTransactions(transactions);
		return ResponseEntity.ok().body(response);
	}

	
	/** 
	 * @param searchTransaction(
	 * @return ResponseEntity<ResponseBodyCustom>
	 */
	@GetMapping("/transactions/{accountIban}")
	@ApiOperation(value = "Search a Transaction", notes = "If any error occurs, it will be intercepted by an error handler")
	public ResponseEntity<ResponseBodyCustom> searchTransaction(@PathVariable String accountIban,
			@RequestParam String sortamount) {
		List<TransactionDTO> listTransactions = transactionSearchService.searchTransaction(accountIban, sortamount);
		ResponseBodyCustom response = new ResponseBodyCustom();
		response.setMessage("Transaction found");
		response.setStatus(HttpStatus.OK);
		response.setTimestamp(LocalDateTime.now());
		response.setTransactions(listTransactions);
		return ResponseEntity.ok().body(response);
	}

	
	/** 
	 * @param transactionStatus(
	 * @return ResponseEntity<ResponseBodyCustom>
	 */
	@GetMapping("/transactions/status/{reference}/{channel}")
	@ApiOperation(value = "Search a transaction by reference and status", notes = "If any error occurs, it will be intercepted by an error handler")
	public ResponseEntity<ResponseBodyCustom> transactionStatus(@PathVariable @Valid String reference,
			@PathVariable String channel) {
		TransactionDTO t = transactionSearchService.getTransactionStatus(reference,
				ChannelStatusEnum.fromString(channel).name());
		ResponseBodyCustom response = new ResponseBodyCustom();
		response.setMessage("Transaction found");
		response.setStatus(HttpStatus.OK);
		response.setTimestamp(LocalDateTime.now());
		List<TransactionDTO> transactions = new ArrayList<>();
		transactions.add(t);
		response.setTransactions(transactions);
		return ResponseEntity.ok().body(response);
	}

	
	/** 
	 * @param transactionCreateService
	 */
	@Autowired
	public void setTransactionCreateService(TransactionCreateService transactionCreateService) {
		this.transactionCreateService = transactionCreateService;
	}

	
	/** 
	 * @param transactionSearchService
	 */
	@Autowired
	public void setTransactionSearchService(TransactionSearchService transactionSearchService) {
		this.transactionSearchService = transactionSearchService;
	}

	
	/** 
	 * @param request
	 * @return TransactionDTO
	 */
	private TransactionDTO requestToDto(RequestBodyCustom request) {
		TransactionDTO transactionDTO = new TransactionDTO();
		transactionDTO.setReference(request.getReference());
		transactionDTO.setAccountIban(request.getAccountIban());
		transactionDTO.setDate(request.getDate());
		transactionDTO.setAmount(request.getAmount());
		transactionDTO.setFee(request.getFee());
		transactionDTO.setDescription(request.getDescription());
		if (request.getChannel() != null) {
			transactionDTO.setChannel(ChannelStatusEnum.fromString(request.getChannel()));
		}
		if (request.getStatus() != null) {
			transactionDTO.setStatus(TransactionEnum.fromString(request.getStatus()));
		}
		return transactionDTO;
	}

}
