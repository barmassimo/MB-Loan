package it.massimobarbieri.mbloan.domain;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

@Entity
@Table(name = "mbloan_user")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	@Size(min = 1, max = 50)
	private String name;

	@Column(nullable = false)
	@Size(min = 1, max = 50)
	private String surname;

	@Column(nullable = false, unique = true)
	@Size(min = 6, max = 100)
	@Email
	private String email;

	@Column(nullable = false)
	@Size(min = 6, max = 255)
	private String password;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@OrderBy("creationDate")
	private List<Loan> loans;

	public User() {
		loans = new LinkedList<Loan>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getId() {
		return id;
	}

	public void addLoan(Loan loan) {
		loan.setUser(this);
		loans.add(loan);
	}

	public List<Loan> getLoans() {
		return loans;
	}

	public Loan getLoanById(long id) {
		for (Loan loan : loans)
			if (loan.getId() == id)
				return loan;

		return null;
	}

	public void update(User user) {
		this.name = user.name;
		this.surname = user.surname;
		this.password = user.password;
	}
}