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

	
	/** 
	 * @param accountIBAN
	 * @param orderBy
	 * @return List<TransactionDTO>
	 */
	@Override
	public List<TransactionDTO> searchTransaction(final String accountIBAN, final String orderBy) {
		final List<TransactionEntity> listTransactionsBBDD = transactionDAO.findByAccountIban(accountIBAN,
				new Sort(Sort.Direction.fromString(orderBy), "amount"));
		return listTransactionsBBDD.stream().map(t -> converToDto(t)).collect(Collectors.toList());
	}

	
	/** 
	 * @param reference
	 * @param channel
	 * @return TransactionDTO
	 */
	public TransactionDTO getTransactionStatus(final String reference, final String channel) {
		if (reference == null) {
			throw new TransactionException("Reference is mandatory");
		}
		final TransactionEntity transaction = transactionDAO.getTransactionStatus(reference, channel);
		if (transaction == null) {
			throw new SearchTransactionNotFoundException(reference, TransactionEnum.INVALID.name());
		}
		checkBusinnesCases(channel, transaction);
		return converToDto(transaction);
	}

	
	/** 
	 * @param channel
	 * @param transaction
	 */
	private void checkBusinnesCases(final String channel, final TransactionEntity transaction) {
		if (chanelIsClientOrAtm(channel) && transactionDateIsBeforeToday(transaction)) {
			transaction.setStatus(TransactionEnum.SETTLED.name());
			final BigDecimal newAmoun = amountSubstractingTheFee(transaction.getAmount(), transaction.getFee());
			transaction.setAmount(newAmoun.doubleValue());
		} else if (chanelIsClientOrAtm(channel) && transactionDateIsEqualToday(transaction) || channelIsATMAndTransactionDateIsGreaterToday(
				channel, transaction)) {
			transaction.setStatus(TransactionEnum.PENDING.name());
			final BigDecimal newAmoun = amountSubstractingTheFee(transaction.getAmount(), transaction.getFee());
			transaction.setAmount(newAmoun.doubleValue());
		} else if (ChannelStatusEnum.INTERNAL.name().equalsIgnoreCase(channel) && transactionDateIsBeforeToday(transaction)) {
			transaction.setStatus(TransactionEnum.SETTLED.name());
		} else if (ChannelStatusEnum.INTERNAL.name().equalsIgnoreCase(channel) && transactionDateIsEqualToday(transaction)) {
			transaction.setStatus(TransactionEnum.PENDING.name());
		} else if (ChannelStatusEnum.INTERNAL.name().equalsIgnoreCase(channel) && transactionDateIsGreaterToday(transaction)) {
			transaction.setStatus(TransactionEnum.FUTURE.name());
		} else if (ChannelStatusEnum.CLIENT.name().equalsIgnoreCase(channel) && transactionDateIsGreaterToday(transaction)) {
			transaction.setStatus(TransactionEnum.FUTURE.name());
			final BigDecimal newAmoun = amountSubstractingTheFee(transaction.getAmount(), transaction.getFee());
			transaction.setAmount(newAmoun.doubleValue());
		}
	}

	
	/** 
	 * @param channel
	 * @param transaction
	 * @return boolean
	 */
	private boolean channelIsATMAndTransactionDateIsGreaterToday(final String channel, final TransactionEntity transaction) {
		return ChannelStatusEnum.ATM.name().equalsIgnoreCase(channel) && transactionDateIsGreaterToday(transaction);
	}

	
	/** 
	 * @param tAmount
	 * @param tFee
	 * @return BigDecimal
	 */
	private BigDecimal amountSubstractingTheFee(final Double tAmount, final Double tFee) {
		final BigDecimal amount = BigDecimal.valueOf(tAmount);
		final BigDecimal fee = BigDecimal.valueOf(tFee);
		return amount.subtract(fee);
	}

	
	/** 
	 * @param channel
	 * @return boolean
	 */
	private boolean chanelIsClientOrAtm(final String channel) {
		return ChannelStatusEnum.CLIENT.name().equalsIgnoreCase(channel)
				|| ChannelStatusEnum.ATM.name().equalsIgnoreCase(channel);
	}

	
	/** 
	 * @param transaction
	 * @return boolean
	 */
	private boolean transactionDateIsBeforeToday(final TransactionEntity transaction) {
		final LocalDate today = LocalDate.now();
		final LocalDate transactionDate = transaction.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		return transactionDate.isBefore(today);
	}

	
	/** 
	 * @param transaction
	 * @return boolean
	 */
	private boolean transactionDateIsEqualToday(final TransactionEntity transaction) {

		final LocalDate today = LocalDate.now();
		final LocalDate transactionDate = transaction.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		return today.isEqual(transactionDate);
	}

	
	/** 
	 * @param transaction
	 * @return boolean
	 */
	private boolean transactionDateIsGreaterToday(final TransactionEntity transaction) {

		final LocalDate today = LocalDate.now();
		final LocalDate transactionDate = transaction.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		return transactionDate.isAfter(today);
	}

	
	/** 
	 * @param t
	 * @return TransactionDTO
	 */
	public TransactionDTO converToDto(final TransactionEntity t) {
		return modelMapper.map(t, TransactionDTO.class);
	}
}
