package it.massimobarbieri.mbloan.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import it.massimobarbieri.mbloan.domain.User;
import it.massimobarbieri.mbloan.service.UserService;

public class UserAuthenticationProvider implements AuthenticationProvider {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	public UserAuthenticationProvider() {
		log.info("AuthenticationProvider created.");
	}

	public UserAuthenticationProvider(UserService users) {
		this();
		this.users = users;
	}

	private UserService users;

	@Override
	public Authentication authenticate(Authentication authentication) {
		String username = authentication.getName();
		String password = (String) authentication.getCredentials();

		log.info(String.format("Checking credentials for %s.", username));

		User user = users.findByEmail(username);

		if (user == null || !user.getPassword().equals(password)) {
			log.info(String.format("Authentication failed for %s.", username));
			throw new BadCredentialsException("Authentication failed.");
		}

		log.info(String.format("Authentication ok for %s.", username));
		return new UsernamePasswordAuthenticationToken(username, password, null);
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return true;
	}
}
