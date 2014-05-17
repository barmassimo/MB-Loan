package it.massimobarbieri.mbloan.controller.rest_support;

import org.springframework.hateoas.ResourceSupport;

import it.massimobarbieri.mbloan.domain.Loan;

public class LoanResource extends ResourceSupport {
	public Loan loan;
}