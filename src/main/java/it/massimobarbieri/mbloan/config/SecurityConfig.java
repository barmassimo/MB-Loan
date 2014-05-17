package it.massimobarbieri.mbloan.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import it.massimobarbieri.mbloan.service.UserService;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@EnableWebSecurity
	@Configuration
	@Order(1)
	public static class UISecurityConfig extends WebSecurityConfigurerAdapter {

		@Autowired
		private UserService users;

		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.authenticationProvider(new UserAuthenticationProvider(users));
		}

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers("/res/**");
			web.ignoring().antMatchers("/");
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.antMatcher("/secure/**").authorizeRequests().antMatchers("/secure/**").authenticated().and()
					.formLogin().loginPage("/secure/login").defaultSuccessUrl("/secure/my_loans")
					.failureUrl("/secure/login?error").permitAll();
		}
	}

	@EnableWebSecurity
	@Configuration
	@Order(2)
	public static class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

		@Autowired
		private UserService users;

		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.authenticationProvider(new UserAuthenticationProvider(users));
		}

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers("/api/users");
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.antMatcher("/api/**").authorizeRequests().antMatchers("/api/**").authenticated().and().httpBasic();
			http.csrf().disable();
		}
	}
}