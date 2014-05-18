package it.massimobarbieri.mbloan.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.massimobarbieri.mbloan.Application;
import it.massimobarbieri.mbloan.domain.Loan;
import it.massimobarbieri.mbloan.domain.LoanExtension;
import it.massimobarbieri.mbloan.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class UserRepositoryTest {

	@Autowired
	UserRepository users;

	@Before
	public void CleanupDb() {
		users.deleteAll();
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void usersAddTwice() {

		User user = getTestUser();
		User user2 = getTestUser();

		users.saveAndFlush(user);
		users.saveAndFlush(user2);
	}
	
	@Test
	public void deleteCascade() {

		User user = getTestUser();
		Loan loan = getTestLoan();
		
		user.addLoan(loan);
		
		loan.extend(new LoanExtension());
		
		users.saveAndFlush(user);
		
		assertThat(users.findAll().size(), is(1)); 
		
		users.delete(user);
		
		assertThat(users.findAll().size(), is(0)); 
		
	}

	@Test
	public void findByEmail() {

		User user = getTestUser();

		assertThat(users.findByEmail(user.getEmail()).size(), is(0));

		users.saveAndFlush(user);

		assertThat(users.findByEmail(user.getEmail()).size(), is(1));
	}

	@Test
	public void findLoansFromIpAddressByDate() {
		User user = getTestUser();

		user.addLoan(getTestLoan());
		user.addLoan(getTestLoan());
		user.addLoan(getTestLoan());

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 1);

		Loan futureLoan = getTestLoan();
		futureLoan.setCreationDate(calendar.getTime());
		user.addLoan(futureLoan); // not included in count

		Loan differentIpLoan = getTestLoan();
		differentIpLoan.setIpAddress("192.168.0.1");
		user.addLoan(differentIpLoan); // not included in count

		users.saveAndFlush(user);

		Calendar from = Calendar.getInstance();
		from.add(Calendar.MINUTE, -30);

		Calendar to = Calendar.getInstance();
		to.add(Calendar.MINUTE, 30);

		List<Loan> loans = users.findLoansFromIpAddressByDate("192.168.0.42", from.getTime(), to.getTime());

		assertThat(loans.size(), is(3));
	}

	private User getTestUser() {
		User user = new User();
		user.setName("John");
		user.setSurname("Smith");
		user.setEmail("john@massimobarbieri.it");
		user.setPassword("john-secret");
		return user;
	}

	private Loan getTestLoan() {
		Loan loan = new Loan();
		loan.setAmount(new BigDecimal(100));
		loan.setDuration(50);
		loan.setIpAddress("192.168.0.42");
		return loan;
	}

}
