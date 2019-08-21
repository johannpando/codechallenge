package com.orangebank.codechallenge.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.orangebank.codechallenge.model.TransactionEntity;

@Repository
public interface TransactionDAO extends CrudRepository<TransactionEntity, String> {

	List<TransactionEntity> findByAccountIban(String accountIban, Sort sort);

	@Query("select t from TransactionEntity t where t.reference=?1 and t.channel=?2")
	TransactionEntity getTransactionStatus(String reference, String channel);

}
