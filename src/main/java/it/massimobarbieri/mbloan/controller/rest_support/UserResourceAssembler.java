package it.massimobarbieri.mbloan.controller.rest_support;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

import it.massimobarbieri.mbloan.controller.ApiController;
import it.massimobarbieri.mbloan.domain.User;

@Service
public class UserResourceAssembler extends ResourceAssemblerSupport<User, UserResource> {

	public UserResourceAssembler() {
		super(ApiController.class, UserResource.class);
	}

	@Override
	public UserResource toResource(User user) {
		UserResource resource = instantiateResource(user);
		resource.user = user;
		long id = user.getId();

		resource.add(linkTo(ApiController.class).slash("users").slash(id).withSelfRel());

		return resource;
	}

}
