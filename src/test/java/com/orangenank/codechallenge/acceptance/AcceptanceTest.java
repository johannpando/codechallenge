package com.orangenank.codechallenge.acceptance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import com.orangebank.codechallenge.OrangebankCodechallengeApplication;
import com.orangebank.codechallenge.util.ChannelStatusEnum;
import com.orangebank.codechallenge.util.TransactionEnum;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrangebankCodechallengeApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class AcceptanceTest {

	@LocalServerPort
	private Integer port;

	/**
	 * @Given: A transaction that is not stored in our system.
	 * @When: I check the status from any channel
	 * @Then: The system returns the status 'INVALID'
	 */
	@Test
	public void givenNotTransactionBD_whenStatusAnyChannel_thenReturnINVALIDStatus() {
		try {
			String uri = "http://localhost:".concat(port.toString()).concat("/api/transactions/status/XXX111/CLIENT");
			HttpGet request = new HttpGet(uri);
			HttpClient httpclient = HttpClientBuilder.create().build();
			HttpResponse response = httpclient.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuilder result = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			String jsonExpected = "{\"reference\":\"XXX111\",\"status\":\"INVALID\"}";
			JSONObject jsonResult = new JSONObject(result.toString());

			JSONAssert.assertEquals(jsonExpected, jsonResult, false);

		} catch (Exception e) {
			fail();
		}
	}

	/**
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws JSONException
	 * @Given: A transaction that is stored in our system
	 * @When: I check the status from CLIENT or ATM channel And the transaction date
	 *        is before today
	 * @Then: The system returns the status 'SETTLED' And the amount substracting
	 *        the fee
	 */
	@Test
	public void givenTransactionBD_whenCheckStatusFromChannelClientOrATMDateBeforeToday_thenReturnSETTLEDStatus()
			throws ClientProtocolException, IOException, JSONException {

		String host = "http://localhost:".concat(port.toString());
		String reference = "123456A";
		String channel = "CLIENT";
		LocalDateTime yesterday = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.now());
		createTransaction(host, reference, channel, yesterday.toString());

		try {

			String uri = host.concat("/api/transactions/status/").concat(reference).concat("/").concat(channel);
			HttpGet request = new HttpGet(uri);
			HttpClient httpclient = HttpClientBuilder.create().build();
			HttpResponse response = httpclient.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuilder result = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			JSONObject resultSearch = new JSONObject(result.toString());
			JSONArray jsonArray = resultSearch.getJSONArray("transactions");
			JSONObject jsonValue = (JSONObject) jsonArray.get(0);
			String status = jsonValue.optString("status");

			assertEquals(TransactionEnum.SETTLED.name(), status);

		} catch (Exception e) {
			fail();
		}
	}

	/**
	 * @Given: A transaction that is stored in our system
	 * @When: I check the status from INTERNAL channel And the transaction date is
	 *        before today
	 * @Then: The system returns the status 'SETTLED' And the amount And the fee
	 */
	@Test
	public void givenTransactionBD_whenCheckStatusFromChannelINTERNAL_thenReturnSETTLEDStatus()
			throws ClientProtocolException, IOException, JSONException {

		String host = "http://localhost:".concat(port.toString());
		String reference = "123456B";
		String channel = "INTERNAL";
		LocalDateTime yesterday = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.now());
		JSONObject transactionCreated = createTransaction(host, reference, channel, yesterday.toString());

		try {

			String uri = host.concat("/api/transactions/status/").concat(reference).concat("/").concat(channel);
			HttpGet request = new HttpGet(uri);
			HttpClient httpclient = HttpClientBuilder.create().build();
			HttpResponse response = httpclient.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuilder result = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			JSONObject resultSearch = new JSONObject(result.toString());
			JSONArray jsonArray = resultSearch.getJSONArray("transactions");
			JSONObject jsonValue = (JSONObject) jsonArray.get(0);
			String status = jsonValue.optString("status");
			String amountTransactionSearched = jsonValue.optString("amount");
			String feeTransactionSearched = jsonValue.optString("fee");

			assertEquals(TransactionEnum.SETTLED.name(), status);

			JSONArray jsonArrayTransactionCreated = transactionCreated.getJSONArray("transactions");
			JSONObject jsonGetZero = (JSONObject) jsonArrayTransactionCreated.get(0);
			String amountTransactionCreated = jsonGetZero.optString("amount");
			String feeTransactionCreated = jsonGetZero.optString("fee");

			assertEquals(amountTransactionCreated, amountTransactionSearched);
			assertEquals(feeTransactionCreated, feeTransactionSearched);

		} catch (Exception e) {
			fail();
		}
	}

	/**
	 * @Given: A transaction that is stored in our system
	 * @When: I check the status from CLIENT or ATM channel And the transaction date
	 *        is equals to today
	 * @Then: The system returns the status 'PENDING' And the amount substracting
	 *        the fee
	 */
	@Test
	public void givenTransactionBD_whenCheckStatusFromChannelCLIENTOrATMAndDAteIsToday_thenReturnPENDINGStatus()
			throws ClientProtocolException, IOException, JSONException {

		String host = "http://localhost:".concat(port.toString());
		String reference = "123456C";
		String channel = ChannelStatusEnum.ATM.name();

		LocalDateTime today = LocalDateTime.of(LocalDate.now(), LocalTime.now());

		createTransaction(host, reference, channel, today.toString());

		try {

			String uri = host.concat("/api/transactions/status/").concat(reference).concat("/").concat(channel);
			HttpGet request = new HttpGet(uri);
			HttpClient httpclient = HttpClientBuilder.create().build();
			HttpResponse response = httpclient.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuilder result = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			JSONObject resultSearch = new JSONObject(result.toString());
			JSONArray jsonArray = resultSearch.getJSONArray("transactions");
			JSONObject jsonValue = (JSONObject) jsonArray.get(0);
			String status = jsonValue.optString("status");

			assertEquals(TransactionEnum.PENDING.name(), status);

		} catch (Exception e) {
			fail();
		}
	}

	/**
	 * @Given: A transaction that is stored in our system
	 * @When: I check the status from INTERNAL channel And the transaction date is
	 *        equals to today
	 * @Then: The system returns the status 'PENDING' And the amount And the fee
	 */
	@Test
	public void givenTransactionBD_whenCheckStatusFromChannelINTERNALAndDateIsToday_thenReturnPENDINGStatus()
			throws ClientProtocolException, IOException, JSONException {

		String host = "http://localhost:".concat(port.toString());
		String reference = "123456D";
		String channel = ChannelStatusEnum.INTERNAL.name();

		LocalDateTime today = LocalDateTime.of(LocalDate.now(), LocalTime.now());

		createTransaction(host, reference, channel, today.toString());

		try {

			String uri = host.concat("/api/transactions/status/").concat(reference).concat("/").concat(channel);
			HttpGet request = new HttpGet(uri);
			HttpClient httpclient = HttpClientBuilder.create().build();
			HttpResponse response = httpclient.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuilder result = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			JSONObject resultSearch = new JSONObject(result.toString());
			JSONArray jsonArray = resultSearch.getJSONArray("transactions");
			JSONObject jsonValue = (JSONObject) jsonArray.get(0);
			String status = jsonValue.optString("status");

			assertEquals(TransactionEnum.PENDING.name(), status);

		} catch (Exception e) {
			fail();
		}
	}

	/**
	 * @Given: A transaction that is stored in our system
	 * @When: I check the status from CLIENT channel And the transaction date is
	 *        greater than today
	 * @Then: The system returns the status 'FUTURE' And the amount substracting the
	 *        fee
	 */
	@Test
	public void givenTransactionBD_whenCheckStatusFromChannelCLIENTAndDateIsGreaterToday_thenReturnFUTUREStatus()
			throws ClientProtocolException, IOException, JSONException {

		String host = "http://localhost:".concat(port.toString());
		String reference = "123456E";
		String channel = ChannelStatusEnum.CLIENT.name();

		LocalDateTime tomorrow = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.now());

		createTransaction(host, reference, channel, tomorrow.toString());

		try {

			String uri = host.concat("/api/transactions/status/").concat(reference).concat("/").concat(channel);
			HttpGet request = new HttpGet(uri);
			HttpClient httpclient = HttpClientBuilder.create().build();
			HttpResponse response = httpclient.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuilder result = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			JSONObject resultSearch = new JSONObject(result.toString());
			JSONArray jsonArray = resultSearch.getJSONArray("transactions");
			JSONObject jsonValue = (JSONObject) jsonArray.get(0);
			String status = jsonValue.optString("status");

			assertEquals(TransactionEnum.FUTURE.name(), status);

		} catch (Exception e) {
			fail();
		}
	}

	/**
	 * @Given: A transaction that is stored in our system
	 * @When: I check the status from ATM channel And the transaction date is
	 *        greater than today
	 * @Then: The system returns the status 'PENDING' And the amount substracting
	 *        the fee
	 */
	@Test
	public void givenTransactionBD_whenCheckStatusFromChannelATMAndDateIsGreaterToday_thenReturnPENDINGStatus()
			throws ClientProtocolException, IOException, JSONException {

		String host = "http://localhost:".concat(port.toString());
		String reference = "123456F";
		String channel = ChannelStatusEnum.ATM.name();

		LocalDateTime tomorrow = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.now());

		createTransaction(host, reference, channel, tomorrow.toString());

		try {

			String uri = host.concat("/api/transactions/status/").concat(reference).concat("/").concat(channel);
			HttpGet request = new HttpGet(uri);
			HttpClient httpclient = HttpClientBuilder.create().build();
			HttpResponse response = httpclient.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuilder result = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			JSONObject resultSearch = new JSONObject(result.toString());
			JSONArray jsonArray = resultSearch.getJSONArray("transactions");
			JSONObject jsonValue = (JSONObject) jsonArray.get(0);
			String status = jsonValue.optString("status");

			assertEquals(TransactionEnum.PENDING.name(), status);

		} catch (Exception e) {
			fail();
		}
	}

	/**
	 * @Given: A transaction that is stored in our system
	 * @When: I check the status from INTERNAL channel And the transaction date is
	 *        greater than today
	 * @Then: The system returns the status 'FUTURE' And the amount And the fee
	 */
	@Test
	public void givenTransactionBD_whenCheckStatusFromChannelinternalAndDateIsGreaterToday_thenReturnPENDINGStatus()
			throws ClientProtocolException, IOException, JSONException {

		String host = "http://localhost:".concat(port.toString());
		String reference = "123456G";
		String channel = ChannelStatusEnum.INTERNAL.name();

		LocalDateTime tomorrow = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.now());

		createTransaction(host, reference, channel, tomorrow.toString());

		try {

			String uri = host.concat("/api/transactions/status/").concat(reference).concat("/").concat(channel);
			HttpGet request = new HttpGet(uri);
			HttpClient httpclient = HttpClientBuilder.create().build();
			HttpResponse response = httpclient.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuilder result = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			JSONObject resultSearch = new JSONObject(result.toString());
			JSONArray jsonArray = resultSearch.getJSONArray("transactions");
			JSONObject jsonValue = (JSONObject) jsonArray.get(0);
			String status = jsonValue.optString("status");

			assertEquals(TransactionEnum.FUTURE.name(), status);

		} catch (Exception e) {
			fail();
		}
	}

	private JSONObject createTransaction(String host, String reference, String channel, String date)
			throws ClientProtocolException, IOException, JSONException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(host.concat("/api/transactions"));
		String jsonBody = "{\r\n" + "    \"reference\": \"" + reference + "\",\r\n"
				+ "    \"account_iban\": \"ES9820385778983000760236\",\r\n" + "    \"date\": \"" + date + "\",\r\n"
				+ "    \"amount\": 7643,\r\n" + "    \"fee\": 3.18,\r\n"
				+ "    \"description\": \"Restaurant payment\",\r\n" + "    \"channel\": \"" + channel + "\"\r\n" + "}";

		StringEntity entity = new StringEntity(jsonBody);

		post.setEntity(entity);
		post.setHeader("Accept", "application/json");
		post.setHeader("Content-type", "application/json");

		CloseableHttpResponse response = client.execute(post);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuilder result = new StringBuilder();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		client.close();
		return new JSONObject(result.toString());
	}
}
