package com.orangebank.codechallenge.service;

import com.orangebank.codechallenge.dto.TransactionDTO;

public interface TransactionCreateService {

	TransactionDTO createTransaction(TransactionDTO transactionDto);

}
