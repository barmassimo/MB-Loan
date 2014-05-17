package it.massimobarbieri.mbloan.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.massimobarbieri.mbloan.domain.Loan;
import it.massimobarbieri.mbloan.domain.LoanExtension;
import it.massimobarbieri.mbloan.domain.User;
import it.massimobarbieri.mbloan.exception.ApplicationLogicException;
import it.massimobarbieri.mbloan.repository.UserRepository;

@Service
@Transactional
public class UserServiceImpl extends CrudServiceImpl<User, Long> implements UserService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public User findByEmail(String email) {
		List<User> users = ((UserRepository) repo).findByEmail(email);

		if (users.size() == 0)
			return null;
		else
			return users.get(0);
	}

	@Override
	public void save(User user) throws ApplicationLogicException {

		if (user.getId() == 0) {

			User existingUser = findByEmail(user.getEmail());
			if (existingUser != null) {
				log.info(String.format("User %s already registered.", user.getEmail()));
				throw new ApplicationLogicException("An user with this email is already registered.");
			}
		}

		repo.saveAndFlush(user);

		log.info(String.format("User %s created.", user.getEmail()));
	}

	@Override
	public void createNewLoan(User user, Loan loan) throws ApplicationLogicException {
		checkLoanRequest(user, loan);
		user.addLoan(loan);

		repo.saveAndFlush(user);

		log.info(String.format("Loan for user %s created.", user.getEmail()));
	}

	@Override
	public void extendLoan(Loan loan, LoanExtension loanExtension) throws ApplicationLogicException {
		loan.extend(loanExtension);
		repo.saveAndFlush(loan.getUser());

		log.info(String.format("Loan extension for user %s created.", loan.getUser().getEmail()));
	}

	private void checkLoanRequest(User user, Loan loan) throws ApplicationLogicException {

		if (!checkMaxAmountDuringNight(loan)) {
			log.warn(String.format("Application rejected for user %s: loans with max amount are not pemitted now.",
					user.getEmail()));
			throw new ApplicationLogicException("Loans with max amount are not pemitted now. Application rejected.");
		}

		if (!checkRequestsFromTheSameIpAddress(loan)) {
			log.warn(String.format(
					"Application rejected for user %s: too many applications per day from the same IP address.",
					user.getEmail()));
			throw new ApplicationLogicException(
					"Too many applications per day from the same IP address. Application rejected.");
		}
	}

	private boolean checkMaxAmountDuringNight(Loan loan) {

		String hourMaxAmountForbiddenFrom = "0000";
		String hourMaxAmountForbiddenTo = "0600";

		if (loan.getAmount().equals(new BigDecimal(Loan.AMOUNT_MAX))) {

			String dateFormat = "HHmm";

			SimpleDateFormat sdfHour = new SimpleDateFormat(dateFormat);
			String hour = sdfHour.format(loan.getCreationDate());

			if ((hour.compareTo(hourMaxAmountForbiddenFrom) >= 0) && (hour.compareTo(hourMaxAmountForbiddenTo) <= 0))
				return false;
		}

		return true;
	}

	private boolean checkRequestsFromTheSameIpAddress(Loan loan) {

		int maxApplicationAdmitted = 3;

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date dateFrom = calendar.getTime();

		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date dateTo = calendar.getTime();

		List<Loan> loans = ((UserRepository) repo).findLoansFromIpAddressByDate(loan.getIpAddress(), dateFrom, dateTo);

		if (loans.size() >= maxApplicationAdmitted)
			return false;

		return true;
	}
}