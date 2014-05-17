package it.massimobarbieri.mbloan.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.massimobarbieri.mbloan.domain.User;
import it.massimobarbieri.mbloan.exception.ApplicationLogicException;
import it.massimobarbieri.mbloan.service.UserService;

@Controller
public class RootController extends WebMvcConfigurerAdapter {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserService users;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView home(@ModelAttribute User user) {
		return new ModelAndView("home");
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ModelAndView register(@Valid User user, BindingResult bindingResult, RedirectAttributes redirect) {
		if (bindingResult.hasErrors()) {
			return new ModelAndView("home");
		}

		try {
			users.save(user);
		} catch (ApplicationLogicException e) {
			return new ModelAndView("home", "warningMessage", e.getMessage());
		}

		redirect.addFlashAttribute("normalMessage", "You registration was accepted. You can login now.");
		ModelAndView mv = new ModelAndView("redirect:/secure/login");
		return mv;

	}
}
