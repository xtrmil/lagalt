package se.experis.com.case2020.lagalt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import se.experis.com.case2020.lagalt.services.AuthService;

@SpringBootTest
class LagaltApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void testUsernameValidation() {
		var sut = new AuthService();

		assertEquals(false, sut.isValidUsername(""));
		assertEquals(false, sut.isValidUsername("  "));
		assertEquals(false, sut.isValidUsername("some-User"));
		assertEquals(false, sut.isValidUsername("some.name"));
		assertEquals(false, sut.isValidUsername("someuser}"));
		assertEquals(false, sut.isValidUsername("someuserwithAVeryLonglonglongname"));

		assertEquals(true, sut.isValidUsername("Bumpfel"));
		assertEquals(true, sut.isValidUsername("09223Bumpfel"));
		assertEquals(true, sut.isValidUsername("_someUser1337"));
		assertEquals(true, sut.isValidUsername("SomeUser_09"));
	}
}
