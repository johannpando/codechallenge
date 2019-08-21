package com.orangebank.codechallenge.error;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.orangebank.codechallenge.domain.ErrorResponse;
import com.orangebank.codechallenge.exception.AccountBalanceBellowZeroNotAllowedException;
import com.orangebank.codechallenge.exception.ChannelStatusEnumNotFoundException;
import com.orangebank.codechallenge.exception.SearchTransactionNotFoundException;
import com.orangebank.codechallenge.exception.TransactionNotFoundException;
import com.orangebank.codechallenge.exception.TransactionStatusEnumNotFoundException;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	private static final String ERRORS = "errors";
	private static final String STATUS = "status";
	private static final String TIMESTAMP = "timestamp";
	private static final String BAD_REQUEST = "BAD_REQUEST";

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public final ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
			WebRequest request) {
		List<String> details = ex.getConstraintViolations().stream().map(e -> e.getMessage())
				.collect(Collectors.toList());

		ErrorResponse error = new ErrorResponse(BAD_REQUEST, details);
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		Map<String, Object> body = new LinkedHashMap<>();
		body.put(TIMESTAMP, LocalDateTime.now());
		body.put(STATUS, status.value());

		// Get all errors
		List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(x -> x.getDefaultMessage())
				.collect(Collectors.toList());

		body.put(ERRORS, errors);

		return new ResponseEntity<>(body, headers, status);

	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		Map<String, Object> body = new LinkedHashMap<>();
		body.put(TIMESTAMP, LocalDateTime.now());
		body.put(STATUS, HttpStatus.BAD_REQUEST);

		// Get all errors
		List<String> errors = Arrays.asList(ex.getMessage());

		body.put(ERRORS, errors);

		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

	}

	@ExceptionHandler(AccountBalanceBellowZeroNotAllowedException.class)
	protected ResponseEntity<Object> accountBalanceException(HttpServletResponse response, RuntimeException ex) {

		Map<String, Object> body = new LinkedHashMap<>();
		body.put(TIMESTAMP, LocalDateTime.now());
		body.put(STATUS, HttpStatus.BAD_REQUEST);

		// Get all errors
		List<String> errors = Arrays.asList(ex.getMessage());

		body.put(ERRORS, errors);

		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

	}

	@ExceptionHandler(TransactionNotFoundException.class)
	public ResponseEntity<Object> springHandleNotFound(HttpServletResponse response, RuntimeException ex) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put(TIMESTAMP, LocalDateTime.now());
		body.put(STATUS, HttpStatus.NOT_FOUND);

		// Get all errors
		List<String> errors = Arrays.asList("Transaction not found", ex.getMessage());

		body.put(ERRORS, errors);

		return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(SearchTransactionNotFoundException.class)
	public ResponseEntity<Object> searchTransactionNotFoundException(HttpServletResponse response,
			SearchTransactionNotFoundException ex) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("reference", ex.getReference());
		body.put(STATUS, ex.getStatus());

		return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler({ TransactionStatusEnumNotFoundException.class, ChannelStatusEnumNotFoundException.class })
	public ResponseEntity<Object> springHandleNotFoundEnum(HttpServletResponse response, RuntimeException ex) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put(TIMESTAMP, LocalDateTime.now());
		body.put(STATUS, HttpStatus.NOT_FOUND);

		// Get all errors
		List<String> errors = Arrays.asList("Enum not found", ex.getMessage());

		body.put(ERRORS, errors);

		return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
	}
}
