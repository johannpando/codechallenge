package com.orangebank.codechallenge.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orangebank.codechallenge.dto.TransactionDTO;
import com.orangebank.codechallenge.exception.AccountBalanceBellowZeroNotAllowedException;
import com.orangebank.codechallenge.model.TransactionEntity;
import com.orangebank.codechallenge.repository.TransactionDAO;

@Service
public class TransactionCreateServiceImpl implements TransactionCreateService {

	@Autowired
	private TransactionDAO transactionDAO;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public TransactionDTO createTransaction(TransactionDTO transactionDto) {

		TransactionEntity transaction = dtoToEntity(transactionDto);
		Optional<TransactionEntity> transactionRetrieved = transactionDAO.findById(transactionDto.getReference());

		Double amountRetrievedBBDD = BigDecimal.ZERO.doubleValue();
		if (transactionRetrieved.isPresent()) {
			if (transactionDto.getAmount() < 0
					&& transactionRetrieved.get().getAmount() - Math.abs(transactionDto.getAmount()) < 0) {
				throw new AccountBalanceBellowZeroNotAllowedException("Account Balance bellow zero is not allowed");
			}
			amountRetrievedBBDD = transactionRetrieved.get().getAmount();
		} else {
			if (transactionDto.getAmount() < 0) {
				throw new AccountBalanceBellowZeroNotAllowedException("Account Balance bellow zero is not allowed");
			}
		}
		BigDecimal newAmount = BigDecimal.valueOf(amountRetrievedBBDD);
		newAmount = newAmount.add(BigDecimal.valueOf(transactionDto.getAmount()));
		transaction.setAmount(newAmount.doubleValue());

		return converToDto(transactionDAO.save(transaction));
	}

	private TransactionEntity dtoToEntity(TransactionDTO transactionDto) {
		TransactionEntity transaction = new TransactionEntity();
		if (transactionDto.getReference() == null) {
			UUID uuid = UUID.randomUUID();
			transactionDto.setReference(uuid.toString());
		}
		transaction.setReference(transactionDto.getReference());
		transaction.setAccountIban(transactionDto.getAccountIban());
		transaction.setAmount(transactionDto.getAmount());
		if (transactionDto.getChannel() != null) {
			transaction.setChannel(transactionDto.getChannel().name());
		}
		transaction.setDescription(transactionDto.getDescription());
		transaction.setFee(transactionDto.getFee());

		if (transactionDto.getStatus() != null) {
			transaction.setStatus(transactionDto.getStatus().name());
		}
		transaction.setDate(transactionDto.getDate());
		return transaction;
	}

	private TransactionDTO converToDto(TransactionEntity t) {

		return modelMapper.map(t, TransactionDTO.class);
	}

}
