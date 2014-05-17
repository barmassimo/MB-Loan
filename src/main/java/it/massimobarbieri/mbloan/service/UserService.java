package it.massimobarbieri.mbloan.service;

import it.massimobarbieri.mbloan.domain.Loan;
import it.massimobarbieri.mbloan.domain.LoanExtension;
import it.massimobarbieri.mbloan.domain.User;
import it.massimobarbieri.mbloan.exception.ApplicationLogicException;

public interface UserService extends CrudService<User, Long> {

	User findByEmail(String email);

	void createNewLoan(User user, Loan loan) throws ApplicationLogicException;

	void extendLoan(Loan loan, LoanExtension loanExtension) throws ApplicationLogicException;
}