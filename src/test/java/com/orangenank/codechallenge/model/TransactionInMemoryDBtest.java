package com.orangenank.codechallenge.model;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.UUID;

import javax.annotation.Resource;
import javax.validation.ConstraintDeclarationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import com.orangebank.codechallenge.TransactionJpaConfig;
import com.orangebank.codechallenge.exception.AccountBalanceBellowZeroNotAllowedException;
import com.orangebank.codechallenge.exception.ChannelStatusEnumNotFoundException;
import com.orangebank.codechallenge.exception.TransactionStatusEnumNotFoundException;
import com.orangebank.codechallenge.model.TransactionEntity;
import com.orangebank.codechallenge.repository.TransactionDAO;
import com.orangebank.codechallenge.util.ChannelStatusEnum;
import com.orangebank.codechallenge.util.TransactionEnum;

//@RunWith(SpringJUnit4ClassRunner.class)
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TransactionJpaConfig.class }, loader = AnnotationConfigContextLoader.class)
@Transactional
public class TransactionInMemoryDBtest {

	@Resource
	private TransactionDAO transactionDAO;

	@Test
	public void givenTransactionWhitReference_whenSave_thenGetOk() {
		TransactionEntity transaction = new TransactionEntity();
		transaction.setAccountIban("77788899AAAA");
		transaction.setAmount(12.0);
		transaction.setChannel(ChannelStatusEnum.ATM.name());
		transaction.setDate(Calendar.getInstance().getTime());
		transaction.setDescription("MY description");
		transaction.setFee(12.1);
		final String reference = "123456A";
		transaction.setReference(reference);
		transaction.setStatus(TransactionEnum.FUTURE.name());

		transactionDAO.save(transaction);

		assertEquals(reference, transactionDAO.findById(reference).get().getReference());
	}

	@Test
	public void givenTransactionWhitoutReference_whenSave_thenGetOk() {
		TransactionEntity transaction = new TransactionEntity();
		UUID uuid = UUID.randomUUID();
		transaction.setReference(uuid.toString());
		transaction.setAccountIban("77788899AAAA");
		transaction.setAmount(12.0);
		transaction.setChannel(ChannelStatusEnum.ATM.name());
		transaction.setDate(Calendar.getInstance().getTime());
		transaction.setDescription("MY description");
		transaction.setFee(12.1);
		transaction.setStatus(TransactionEnum.FUTURE.name());

		TransactionEntity t = transactionDAO.save(transaction);

		assertNotNull(t);
		assertNotNull(t.getReference());
	}

	@Test
	public void givenTransactionWhitoutAmount_whenSave_thenGetKo() {
		TransactionEntity transaction = new TransactionEntity();
		transaction.setReference("125364A");
		transaction.setAccountIban("77788899AAAA");
		transaction.setChannel(ChannelStatusEnum.ATM.name());
		transaction.setDate(Calendar.getInstance().getTime());
		transaction.setDescription("MY description");
		transaction.setFee(12.1);
		transaction.setStatus(TransactionEnum.FUTURE.name());

		transactionDAO.save(transaction);
		assertThatExceptionOfType(ConstraintDeclarationException.class).isThrownBy(() -> {
			throw new ConstraintDeclarationException("Amount field is mandatory");
		}).withMessage("Amount field is mandatory");
	}

	@Test
	public void givenTransactionWhitStatusWrong_whenSave_thenGetKo() {
		TransactionEntity transaction = new TransactionEntity();
		transaction.setReference("125364A");
		transaction.setAccountIban("77788899AAAA");
		transaction.setChannel(ChannelStatusEnum.ATM.name());
		transaction.setDate(Calendar.getInstance().getTime());
		transaction.setDescription("MY description");
		transaction.setFee(12.1);
		transaction.setStatus("STATUS_XXXX");

		transactionDAO.save(transaction);
		assertThatExceptionOfType(TransactionStatusEnumNotFoundException.class).isThrownBy(() -> {
			throw new TransactionStatusEnumNotFoundException("Enum not exist");
		}).withMessage("Enum not exist");
	}

	@Test
	public void givenTransactionWhitChanmelEnumWrong_whenSave_thenGetKo() {
		TransactionEntity transaction = new TransactionEntity();
		transaction.setReference("125364A");
		transaction.setAccountIban("77788899AAAA");
		transaction.setChannel("CHANNEL_NRO_5");
		transaction.setDate(Calendar.getInstance().getTime());
		transaction.setDescription("MY description");
		transaction.setFee(12.1);
		transaction.setStatus(TransactionEnum.FUTURE.name());

		transactionDAO.save(transaction);
		assertThatExceptionOfType(ChannelStatusEnumNotFoundException.class).isThrownBy(() -> {
			throw new ChannelStatusEnumNotFoundException("Enum not exist");
		}).withMessage("Enum not exist");
	}

	@Test
	public void givenTransactionWhitoutIBAN_whenSave_thenGetKo() {
		TransactionEntity transaction = new TransactionEntity();
		transaction.setReference("125364A");
		transaction.setChannel(ChannelStatusEnum.ATM.name());
		transaction.setDate(Calendar.getInstance().getTime());
		transaction.setDescription("MY description");
		transaction.setFee(12.1);
		transaction.setStatus(TransactionEnum.FUTURE.name());

		transactionDAO.save(transaction);
		assertThatExceptionOfType(ConstraintDeclarationException.class).isThrownBy(() -> {
			throw new ConstraintDeclarationException("IBAN field is mandatory");
		}).withMessage("IBAN field is mandatory");
	}

	@Test
	public void givenTransaction_whenSave_thenGetKo_thenNewComputeAmountIsLessZero_whenUpdate_thenGetKo() {
		TransactionEntity transaction = new TransactionEntity();
		transaction.setReference("125364A");
		transaction.setChannel(ChannelStatusEnum.ATM.name());
		transaction.setDate(Calendar.getInstance().getTime());
		transaction.setDescription("MY description");
		transaction.setAmount(12.0);
		transaction.setFee(12.1);
		transaction.setStatus(TransactionEnum.FUTURE.name());

		transactionDAO.save(transaction);

		transaction.setAmount(-100.0);

		transactionDAO.save(transaction);

		assertThatExceptionOfType(AccountBalanceBellowZeroNotAllowedException.class).isThrownBy(() -> {
			throw new AccountBalanceBellowZeroNotAllowedException("Account Balance Bellow Zero Not Allowed");
		}).withMessage("Account Balance Bellow Zero Not Allowed");
	}

	@Test
	public void givenTransaction_whenSave_thenGetKo_thenNewComputeAmountIsGreaterOrEqualZero_whenUpdate_thenGetOk() {
		TransactionEntity transaction = new TransactionEntity();
		transaction.setReference("125364A");
		transaction.setChannel(ChannelStatusEnum.ATM.name());
		transaction.setDate(Calendar.getInstance().getTime());
		transaction.setDescription("MY description");
		transaction.setAmount(BigDecimal.TEN.doubleValue());
		transaction.setFee(12.1);
		transaction.setStatus(TransactionEnum.FUTURE.name());

		transactionDAO.save(transaction);

		transaction.setAmount(BigDecimal.ONE.doubleValue());

		TransactionEntity transactionUpdate = transactionDAO.save(transaction);

		assertTrue(transactionUpdate.getAmount().compareTo(BigDecimal.ZERO.doubleValue()) >= 0);
	}

}
