package it.massimobarbieri.mbloan.controller.rest_support;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

import it.massimobarbieri.mbloan.controller.ApiController;
import it.massimobarbieri.mbloan.domain.Loan;

@Service
public class LoanResourceAssembler extends ResourceAssemblerSupport<Loan, LoanResource> {

	public LoanResourceAssembler() {
		super(ApiController.class, LoanResource.class);
	}

	@Override
	public LoanResource toResource(Loan loan) {
		LoanResource resource = instantiateResource(loan);
		resource.loan = loan;

		resource.add(linkTo(ApiController.class).slash("loans").slash(loan.getId()).withSelfRel());
		resource.add(linkTo(ApiController.class).slash("loans").slash(loan.getId()).slash("loan_extension")
				.withRel("extend"));

		return resource;
	}

}
