package it.massimobarbieri.mbloan.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.massimobarbieri.mbloan.domain.Loan;
import it.massimobarbieri.mbloan.domain.LoanExtension;
import it.massimobarbieri.mbloan.domain.User;
import it.massimobarbieri.mbloan.exception.ApplicationLogicException;
import it.massimobarbieri.mbloan.service.UserService;

@Controller
@RequestMapping("secure")
public class SecureController extends WebMvcConfigurerAdapter {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserService users;

	@RequestMapping(value = "login", method = RequestMethod.GET)
	public ModelAndView login() {
		return new ModelAndView("login");
	}

	@RequestMapping(value = "my_loans")
	public ModelAndView myLoans() {

		ModelAndView mv = new ModelAndView("my_loans");
		mv.getModel().put("user", getCurrentUser());
		return mv;
	}

	@RequestMapping(value = "loan_request", method = RequestMethod.GET)
	public ModelAndView loanRequest(@ModelAttribute Loan loan) {
		return new ModelAndView("loan_request");
	}

	@RequestMapping(value = "loan_request", method = RequestMethod.POST)
	public ModelAndView doLoanRequest(@Valid Loan loan, BindingResult bindingResult, RedirectAttributes redirect,
			HttpServletRequest request) {
		if (bindingResult.hasErrors()) {
			return new ModelAndView("loan_request");
		}

		User user = getCurrentUser();

		loan.setIpAddress(request.getRemoteAddr());

		try {
			users.createNewLoan(user, loan);

		} catch (ApplicationLogicException e) {
			return new ModelAndView("loan_request", "warningMessage", e.getMessage());
		}

		redirect.addFlashAttribute("normalMessage", "You application was accepted.");
		ModelAndView mv = new ModelAndView("redirect:/secure/my_loans");
		return mv;
	}

	@RequestMapping(value = "loan_extension_request", method = RequestMethod.POST)
	public ModelAndView doLoanExtensionRequest(RedirectAttributes redirect, HttpServletRequest request)
			throws ApplicationLogicException {

		long id = Integer.parseInt(request.getParameter("id"));

		User user = getCurrentUser();

		Loan loan = user.getLoanById(id);

		users.extendLoan(loan, new LoanExtension());

		redirect.addFlashAttribute("normalMessage", "You loan was extended.");
		ModelAndView mv = new ModelAndView("redirect:/secure/my_loans");
		return mv;
	}

	private User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		return users.findByEmail(name);
	}

}
