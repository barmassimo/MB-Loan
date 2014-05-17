package it.massimobarbieri.mbloan.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import it.massimobarbieri.mbloan.controller.rest_support.LoanResource;
import it.massimobarbieri.mbloan.controller.rest_support.LoanResourceAssembler;
import it.massimobarbieri.mbloan.controller.rest_support.RestResponse;
import it.massimobarbieri.mbloan.domain.Loan;
import it.massimobarbieri.mbloan.domain.LoanExtension;
import it.massimobarbieri.mbloan.domain.User;
import it.massimobarbieri.mbloan.exception.ApplicationLogicException;
import it.massimobarbieri.mbloan.service.UserService;

@RestController
@RequestMapping("api")
public class ApiController {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserService users;

	@Autowired
	private LoanResourceAssembler loanResourceAssembler;

	@RequestMapping("test")
	public String test() {
		return "OK";
	}

	@RequestMapping(value = "users", method = RequestMethod.POST)
	public ResponseEntity<RestResponse> register(@RequestBody @Valid final User user, HttpServletRequest request)
			throws ApplicationLogicException {

		users.save(user);

		ControllerLinkBuilder builder = linkTo(ApiController.class).slash("loans");

		RestResponse response = RestResponse.Success(user.getId());
		response.add(builder.withRel("loans"));

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Location", builder.toString());

		return new ResponseEntity<RestResponse>(response, responseHeaders, HttpStatus.CREATED);
	}

	@RequestMapping(value = "loans", method = RequestMethod.GET)
	public Set<LoanResource> getLoans() {

		User user = getCurrentUser();

		Set<LoanResource> result = new HashSet<LoanResource>();

		for (Loan l : user.getLoans()) {
			result.add(loanResourceAssembler.toResource(l));
		}

		return result;
	}

	@RequestMapping(value = "loans", method = RequestMethod.POST)
	public ResponseEntity<RestResponse> createLoan(@RequestBody @Valid final Loan loan, HttpServletRequest request)
			throws ApplicationLogicException {

		User user = getCurrentUser();

		loan.setIpAddress(request.getRemoteAddr());
		users.createNewLoan(user, loan);

		ControllerLinkBuilder builder = linkTo(ApiController.class).slash("loans").slash(loan.getId());

		RestResponse response = RestResponse.Success(loan.getId());
		response.add(builder.withSelfRel());

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Location", builder.toString());

		return new ResponseEntity<RestResponse>(response, responseHeaders, HttpStatus.CREATED);
	}

	@RequestMapping(value = "loans/{id}", method = RequestMethod.GET)
	public ResponseEntity<? extends ResourceSupport> getLoan(@PathVariable("id") long id) {

		Loan loan = getCurrentUser().getLoanById(id);

		if (loan == null)
			return handleError(new ApplicationLogicException("Loan " + id + " not found."), HttpStatus.NOT_FOUND);

		LoanResource resource = loanResourceAssembler.toResource(loan);

		return new ResponseEntity<LoanResource>(resource, HttpStatus.OK);
	}

	@RequestMapping(value = "loans/{id}/loan_extension", method = RequestMethod.POST)
	public ResponseEntity<RestResponse> extendLoan(@PathVariable("id") long id) throws ApplicationLogicException {

		User user = getCurrentUser();
		Loan loan = user.getLoanById(id);

		if (loan == null)
			return handleError(new ApplicationLogicException("Loan " + id + " not found."), HttpStatus.NOT_FOUND);

		LoanExtension loanExtension = new LoanExtension();
		users.extendLoan(loan, loanExtension);

		RestResponse response = RestResponse.Success(loanExtension.getId());
		response.add(linkTo(ApiController.class).slash("loans").slash(loan.getId()).withRel("loan"));

		return new ResponseEntity<RestResponse>(response, HttpStatus.OK);
	}

	private User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		return users.findByEmail(name);
	}

	@ExceptionHandler(ApplicationLogicException.class)
	public ResponseEntity<RestResponse> handleApplicationLogicException(ApplicationLogicException e) {
		return handleError(e, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<RestResponse> handleException(Exception e) {
		return handleError(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<RestResponse> handleError(Exception e, HttpStatus status) {
		RestResponse response = RestResponse.Error(e.getClass() + ": " + e.getMessage());
		return new ResponseEntity<RestResponse>(response, status);
	}

}
