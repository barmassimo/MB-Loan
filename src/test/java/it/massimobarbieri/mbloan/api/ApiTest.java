package it.massimobarbieri.mbloan.api;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import it.massimobarbieri.mbloan.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { Application.class })
@WebAppConfiguration
public class ApiTest {

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@Before
	public void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(springSecurityFilterChain).build();
	}

	@Test
	public void home() throws Exception {
		mvc.perform(get("/")).andExpect(status().isOk());
	}

	@Test
	public void forbiddenWithoutCredentials() throws Exception {
		mvc.perform(get("/api/loans")).andExpect(status().is(401));
	}

	@Test
	public void forbiddenWithWrongCredentials() throws Exception {
		mvc.perform(
				get("/api/loans").header("Authorization", getWrongBasicAuthHeader()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is(401));
	}

	@Test
	public void createLoan() throws Exception {
		doCreateLoan();
	}

	@Test
	public void getLoans() throws Exception {

		mvc.perform(get("/api/loans").header("Authorization", getBasicAuthHeader()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void getLoan() throws Exception {
		doCreateLoan();

		mvc.perform(
				get("/api/loans/1").header("Authorization", getBasicAuthHeader()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().string(containsString("{\"id\":1")));
	}

	@Test
	public void getNotExistingLoanFails() throws Exception {
		mvc.perform(
				get("/api/loans/1000").header("Authorization", getBasicAuthHeader()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(content().string(containsString("\"status\":\"error\"")));
	}

	@Test
	public void createUser() throws Exception {
		doCreateUser("massimo").andExpect(status().isCreated()).andExpect(
				content().string(containsString("\"status\":\"success\"")));
	}

	@Test
	public void createUserExistingEmailFails() throws Exception {
		doCreateUser("melissa").andExpect(status().is(400)).andExpect(
				content().string(containsString("\"status\":\"error\"")));
	}

	@Test
	public void extendLoan() throws Exception {
		mvc.perform(
				post("/api/loans/1/loan_extension").header("Authorization", getBasicAuthHeader())
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isOk()).andExpect(content().string(containsString("\"status\":\"success\"")));
	}

	@Test
	public void extendNotExistingLoanFails() throws Exception {
		mvc.perform(
				post("/api/loans/1000/loan_extension").header("Authorization", getBasicAuthHeader())
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isNotFound()).andExpect(content().string(containsString("\"status\":\"error\"")));
	}

	private ResultActions doCreateUser(String name) throws Exception {
		return mvc.perform(post("/api/users")
				.header("Authorization", getBasicAuthHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(
						"{ \"name\": \"" + name + "-name\", \"surname\": \"" + name + "-surname\", \"email\": \""
								+ name + "@massimobarbieri.it\", \"password\": \"" + name + "-secret\" }"));
	}

	private void doCreateLoan() throws Exception {
		mvc.perform(
				post("/api/loans").header("Authorization", getBasicAuthHeader())
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
						.content("{ \"amount\": 1, \"duration\": 42 }")).andExpect(status().isCreated())
				.andExpect(content().string(containsString("\"status\":\"success\"")));
	}

	private String getBasicAuthHeader() {
		return "Basic " + new String(Base64.encodeBase64(("melissa@massimobarbieri.it:melissa-secret").getBytes()));
	}

	private String getWrongBasicAuthHeader() {
		return "Basic " + new String(Base64.encodeBase64(("wrong_user:wrong_pwd").getBytes()));
	}

}
