package it.massimobarbieri.mbloan.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "mbloan_loan")
public class Loan implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final long AMOUNT_MAX = 1000000;
	public static final BigDecimal INTEREST_INITIAL = new BigDecimal(0.03);
	public static final BigDecimal INTEREST_INCREMENT_FACTOR = new BigDecimal(1.5);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@JsonIgnore
	@ManyToOne(optional = false)
	private User user;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@Column(nullable = false)
	@Min(1)
	@Max(AMOUNT_MAX)
	private BigDecimal amount;

	@Column(nullable = false)
	private BigDecimal interest;

	@Column(nullable = false)
	@Min(1)
	private int duration;

	@Column(nullable = false)
	@Size(min = 7, max = 50)
	private String ipAddress;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "loan", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@OrderBy("creationDate")
	private List<LoanExtension> loanExtensions;

	public Loan() {
		loanExtensions = new LinkedList<LoanExtension>();
		interest = INTEREST_INITIAL;
		creationDate = new Date();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public BigDecimal getInterest() {
		return interest;
	}

	public void setInterest(BigDecimal interest) {
		this.interest = interest;
	}

	public void extend(LoanExtension loanExtension) {
		loanExtension.setLoan(this);
		loanExtensions.add(loanExtension);
		interest = interest.multiply(INTEREST_INCREMENT_FACTOR);
		duration += 1;
	}

	public List<LoanExtension> getLoanExtensions() {
		return loanExtensions;
	}

	public long getId() {
		return id;
	}
}