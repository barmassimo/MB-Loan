package it.massimobarbieri.mbloan.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import it.massimobarbieri.mbloan.domain.Loan;
import it.massimobarbieri.mbloan.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	List<User> findByEmail(String email);

	@Query("SELECT l FROM Loan l WHERE l.ipAddress = ?1 AND l.creationDate between ?2 and ?3")
	List<Loan> findLoansFromIpAddressByDate(String ipAddress, Date dateFrom, Date dateTo);
}