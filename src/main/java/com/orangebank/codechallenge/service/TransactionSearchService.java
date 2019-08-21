package com.orangebank.codechallenge.service;

import java.util.List;

import com.orangebank.codechallenge.dto.TransactionDTO;

public interface TransactionSearchService {

	List<TransactionDTO> searchTransaction(String accountIBAN, String orderBy);

	TransactionDTO getTransactionStatus(String reference, String channel);
}
