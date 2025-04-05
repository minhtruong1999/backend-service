package vn.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import vn.java.controller.AuthenticationController;
import vn.java.controller.EmailController;
import vn.java.controller.UserController;

@SpringBootTest
class BackendServiceApplicationTests {

	@InjectMocks
	private AuthenticationController authenticationController;

	@InjectMocks
	private UserController userController;

	@InjectMocks
	private EmailController emailController;

	@Test
	void contextLoads() {
		Assertions.assertNotNull(authenticationController);
		Assertions.assertNotNull(userController);
		Assertions.assertNotNull(emailController);
	}

}
