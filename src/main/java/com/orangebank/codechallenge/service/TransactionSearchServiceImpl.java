package com.orangebank.codechallenge.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.orangebank.codechallenge.dto.TransactionDTO;
import com.orangebank.codechallenge.exception.SearchTransactionNotFoundException;
import com.orangebank.codechallenge.exception.TransactionException;
import com.orangebank.codechallenge.model.TransactionEntity;
import com.orangebank.codechallenge.repository.TransactionDAO;
import com.orangebank.codechallenge.util.ChannelStatusEnum;
import com.orangebank.codechallenge.util.TransactionEnum;

@Service
public class TransactionSearchServiceImpl implements TransactionSearchService {

	@Autowired
	private TransactionDAO transactionDAO;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public List<TransactionDTO> searchTransaction(String accountIBAN, String orderBy) {
		List<TransactionEntity> listTransactionsBBDD = transactionDAO.findByAccountIban(accountIBAN,
				new Sort(Sort.Direction.fromString(orderBy), "amount"));
		return listTransactionsBBDD.stream().map(t -> converToDto(t)).collect(Collectors.toList());
	}

	public TransactionDTO getTransactionStatus(String reference, String channel) {
		if (reference == null) {
			throw new TransactionException("Reference is mandatory");
		}
		TransactionEntity transaction = transactionDAO.getTransactionStatus(reference, channel);
		if (transaction == null) {
			throw new SearchTransactionNotFoundException(reference, TransactionEnum.INVALID.name());
		}
		checkBusinnesCases(channel, transaction);
		return converToDto(transaction);
	}

	private void checkBusinnesCases(String channel, TransactionEntity transaction) {
		if (chanelIsClientOrAtm(channel) && transactionDateIsBeforeToday(transaction)) {
			transaction.setStatus(TransactionEnum.SETTLED.name());
			BigDecimal newAmoun = amountSubstractingTheFee(transaction.getAmount(), transaction.getFee());
			transaction.setAmount(newAmoun.doubleValue());
		}
		if (chanelIsClientOrAtm(channel) && transactionDateIsEqualToday(transaction)) {
			transaction.setStatus(TransactionEnum.PENDING.name());
			BigDecimal newAmoun = amountSubstractingTheFee(transaction.getAmount(), transaction.getFee());
			transaction.setAmount(newAmoun.doubleValue());
		}
		if (ChannelStatusEnum.INTERNAL.name().equalsIgnoreCase(channel) && transactionDateIsBeforeToday(transaction)) {
			transaction.setStatus(TransactionEnum.SETTLED.name());
		}
		if (ChannelStatusEnum.INTERNAL.name().equalsIgnoreCase(channel) && transactionDateIsEqualToday(transaction)) {
			transaction.setStatus(TransactionEnum.PENDING.name());
		}
		if (ChannelStatusEnum.INTERNAL.name().equalsIgnoreCase(channel) && transactionDateIsGreaterToday(transaction)) {
			transaction.setStatus(TransactionEnum.FUTURE.name());
		}
		if (ChannelStatusEnum.CLIENT.name().equalsIgnoreCase(channel) && transactionDateIsGreaterToday(transaction)) {
			transaction.setStatus(TransactionEnum.FUTURE.name());
			BigDecimal newAmoun = amountSubstractingTheFee(transaction.getAmount(), transaction.getFee());
			transaction.setAmount(newAmoun.doubleValue());
		}
		if (ChannelStatusEnum.ATM.name().equalsIgnoreCase(channel) && transactionDateIsGreaterToday(transaction)) {
			transaction.setStatus(TransactionEnum.PENDING.name());
			BigDecimal newAmoun = amountSubstractingTheFee(transaction.getAmount(), transaction.getFee());
			transaction.setAmount(newAmoun.doubleValue());
		}
	}

	private BigDecimal amountSubstractingTheFee(Double tAmount, Double tFee) {
		BigDecimal amount = BigDecimal.valueOf(tAmount);
		BigDecimal fee = BigDecimal.valueOf(tFee);
		return amount.subtract(fee);
	}

	private boolean chanelIsClientOrAtm(String channel) {
		return ChannelStatusEnum.CLIENT.name().equalsIgnoreCase(channel)
				|| ChannelStatusEnum.ATM.name().equalsIgnoreCase(channel);
	}

	private boolean transactionDateIsBeforeToday(TransactionEntity transaction) {
		LocalDate today = LocalDate.now();
		LocalDate transactionDate = transaction.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		return transactionDate.isBefore(today);
	}

	private boolean transactionDateIsEqualToday(TransactionEntity transaction) {

		LocalDate today = LocalDate.now();
		LocalDate transactionDate = transaction.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		return today.isEqual(transactionDate);
	}

	private boolean transactionDateIsGreaterToday(TransactionEntity transaction) {

		LocalDate today = LocalDate.now();
		LocalDate transactionDate = transaction.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		return transactionDate.isAfter(today);
	}

	public TransactionDTO converToDto(TransactionEntity t) {
		return modelMapper.map(t, TransactionDTO.class);
	}
}
