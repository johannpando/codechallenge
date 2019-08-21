package com.orangenank.codechallenge.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.orangebank.codechallenge.controller.TransactionController;
import com.orangebank.codechallenge.domain.RequestBodyCustom;
import com.orangebank.codechallenge.service.TransactionCreateService;
import com.orangebank.codechallenge.service.TransactionSearchService;
import com.orangebank.codechallenge.util.ChannelStatusEnum;
import com.orangebank.codechallenge.util.TransactionEnum;

@RunWith(MockitoJUnitRunner.class)
public class TransactionIntegrationControllerTest {

	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	private MockMvc mvc;

	@Mock
	private TransactionCreateService transactionCreateService;

	@Mock
	private TransactionSearchService transactionSearchService;

	@Before
	public void beforeTest() {
		final TransactionController transactionController = new TransactionController();
		transactionController.setTransactionCreateService(transactionCreateService);
		transactionController.setTransactionSearchService(transactionSearchService);
		mvc = MockMvcBuilders.standaloneSetup(transactionController).build();
	}

	@Test
	public void givenTransactions_whenCreateTransactions_thenReturnJson() throws Exception {

		RequestBodyCustom transactionDTO = new RequestBodyCustom();
		transactionDTO.setAccountIban("8876555");
		transactionDTO.setAmount(12.0);
		transactionDTO.setChannel(ChannelStatusEnum.ATM.name());
		transactionDTO.setDescription("description");
		transactionDTO.setFee(12.0);
		transactionDTO.setStatus(TransactionEnum.FUTURE.name());
		transactionDTO.setReference("ASSDD11");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requesJson = ow.writeValueAsString(transactionDTO);

		mvc.perform(post("/api/transactions").contentType(APPLICATION_JSON_UTF8).content(requesJson))
				.andExpect(status().isOk());
	}
}
