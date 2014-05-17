package it.massimobarbieri.mbloan.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.SpringApplicationConfiguration;

import it.massimobarbieri.mbloan.Application;
import it.massimobarbieri.mbloan.domain.Loan;
import it.massimobarbieri.mbloan.domain.LoanExtension;
import it.massimobarbieri.mbloan.domain.User;
import it.massimobarbieri.mbloan.exception.ApplicationLogicException;
import it.massimobarbieri.mbloan.repository.UserRepository;

@RunWith(MockitoJUnitRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class UserServiceTest {

	@Mock
	UserRepository repo;

	@InjectMocks
	UserService users = new UserServiceImpl();

	@Test
	public void findByEmail() {

		User user1 = getTestUser("user1");

		when(repo.findByEmail(any(String.class))).thenReturn(new LinkedList<User>());
		when(repo.findByEmail(user1.getEmail())).thenReturn(getSingleUserList(user1));

		User u = users.findByEmail(user1.getEmail());
		assertThat(u, is(user1));

		User u2 = users.findByEmail("not@existing.com");
		assertThat(u2, nullValue());

		verify(repo).findByEmail(user1.getEmail());
	}

	@Test
	public void createUser() throws ApplicationLogicException {

		User user1 = getTestUser("user1");
		users.save(user1);

		verify(repo).saveAndFlush(user1);
	}

	@Test
	public void createNewLoanSafeHour() throws ApplicationLogicException {

		Calendar safeHour = Calendar.getInstance();
		safeHour.set(Calendar.HOUR_OF_DAY, 11);

		User user1 = getTestUser("user1");
		Loan loan1 = getTestLoan(new BigDecimal(Loan.AMOUNT_MAX), 42, "127.0.0.1", safeHour.getTime());

		users.createNewLoan(user1, loan1);

		verify(repo).saveAndFlush(user1);
		assertThat(user1.getLoans().size(), is(1));
	}

	@Test(expected = ApplicationLogicException.class)
	public void createNewLoanUnSafeHourMaxAmountFails() throws ApplicationLogicException {

		Calendar safeHour = Calendar.getInstance();
		safeHour.set(Calendar.HOUR_OF_DAY, 2);

		User user1 = getTestUser("user1");
		Loan loan1 = getTestLoan(new BigDecimal(Loan.AMOUNT_MAX), 42, "127.0.0.1", safeHour.getTime());

		users.createNewLoan(user1, loan1);
	}

	@Test
	public void createNewLoanUnsafeHourMaxAmountOk() throws ApplicationLogicException {

		Calendar safeHour = Calendar.getInstance();
		safeHour.set(Calendar.HOUR_OF_DAY, 2);

		User user1 = getTestUser("user1");
		Loan loan1 = getTestLoan(new BigDecimal(Loan.AMOUNT_MAX - 1), 42, "127.0.0.1", safeHour.getTime());

		users.createNewLoan(user1, loan1);

		verify(repo).saveAndFlush(user1);
		assertThat(user1.getLoans().size(), is(1));
	}

	@Test(expected = ApplicationLogicException.class)
	public void createNewLoanWithMaxFromSameIpAddressFails() throws ApplicationLogicException {

		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();

		when(repo.findLoansFromIpAddressByDate(any(String.class), any(Date.class), any(Date.class))).thenReturn(
				getLoanList(now));

		User user1 = getTestUser("user1");
		Loan loan1 = getTestLoan(new BigDecimal(Loan.AMOUNT_MAX - 1), 42, "127.0.0.1", now);

		users.createNewLoan(user1, loan1);
		verify(repo).findLoansFromIpAddressByDate(any(String.class), any(Date.class), any(Date.class));
	}

	@Test
	public void extendLoan() throws ApplicationLogicException {

		User user1 = getTestUser("user1");
		Loan loan1 = getTestLoan(new BigDecimal(Loan.AMOUNT_MAX - 1), 42, "127.0.0.1", Calendar.getInstance().getTime());

		users.createNewLoan(user1, loan1);
		users.extendLoan(loan1, new LoanExtension());

		verify(repo, times(2)).saveAndFlush(user1);
		assertThat(loan1.getLoanExtensions().size(), is(1));
	}

	private List<User> getSingleUserList(User user) {
		LinkedList<User> result = new LinkedList<User>();

		result.add(user);

		return result;
	}

	private User getTestUser(String name) {
		User user = new User();
		user.setName(name);
		user.setSurname("surname");
		user.setEmail("name" + "@massimobarbieri.it");
		user.setPassword(name + "-secret");
		return user;
	}

	private Loan getTestLoan(BigDecimal amount, int duration, String ipAddress, Date creationDate) {
		Loan loan = new Loan();
		loan.setAmount(amount);
		loan.setDuration(duration);
		loan.setIpAddress(ipAddress);
		loan.setCreationDate(creationDate);

		return loan;
	}

	private List<Loan> getLoanList(Date date) {
		LinkedList<Loan> result = new LinkedList<Loan>();

		result.add(getTestLoan(new BigDecimal(Loan.AMOUNT_MAX - 1), 1, "127.0.0.1", date));
		result.add(getTestLoan(new BigDecimal(Loan.AMOUNT_MAX - 1), 2, "127.0.0.1", date));
		result.add(getTestLoan(new BigDecimal(Loan.AMOUNT_MAX - 1), 3, "127.0.0.1", date));

		return result;
	}

}
