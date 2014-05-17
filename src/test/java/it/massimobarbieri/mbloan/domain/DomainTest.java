package it.massimobarbieri.mbloan.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.massimobarbieri.mbloan.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class DomainTest {

	@Test
	public void addLoan() {

		User user1 = getTestUser("user1");
		Loan loan1 = getTestLoan(new BigDecimal(Loan.AMOUNT_MAX - 1), 42, "127.0.0.1", Calendar.getInstance().getTime());

		assertThat(user1.getLoans().size(), is(0));
		assertThat(loan1.getUser(), nullValue());

		user1.addLoan(loan1);

		assertThat(user1.getLoans().size(), is(1));
		assertThat(loan1.getUser(), is(user1));
	}

	@Test
	public void extendLoan() {

		Loan loan1 = getTestLoan(new BigDecimal(Loan.AMOUNT_MAX - 1), 42, "127.0.0.1", Calendar.getInstance().getTime());
		LoanExtension loanExtension1 = new LoanExtension();

		assertThat(loan1.getLoanExtensions().size(), is(0));
		assertThat(loanExtension1.getLoan(), nullValue());
		assertThat(loan1.getInterest(), is(Loan.INTEREST_INITIAL));
		int duration = loan1.getDuration();

		loan1.extend(loanExtension1);

		assertThat(loan1.getLoanExtensions().size(), is(1));
		assertThat(loanExtension1.getLoan(), is(loan1));
		assertThat(loan1.getInterest(), equalTo(Loan.INTEREST_INITIAL.multiply(Loan.INTEREST_INCREMENT_FACTOR)));
		assertThat(loan1.getDuration(), is(duration + 1));
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
}
