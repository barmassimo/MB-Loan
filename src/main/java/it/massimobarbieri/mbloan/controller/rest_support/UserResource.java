package it.massimobarbieri.mbloan.controller.rest_support;

import org.springframework.hateoas.ResourceSupport;

import it.massimobarbieri.mbloan.domain.User;

public class UserResource extends ResourceSupport {
	public User user;
}