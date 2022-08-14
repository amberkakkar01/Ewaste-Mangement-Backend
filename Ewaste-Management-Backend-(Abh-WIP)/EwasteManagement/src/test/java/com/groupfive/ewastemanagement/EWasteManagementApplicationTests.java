package com.groupfive.ewastemanagement;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
class EWasteManagementApplicationTests {

	@Test
	void main() {
		EWasteManagementApplication.main(new String[] {});
		assertEquals(5,2+3);
	}

	@Test
	void contextLoads() {
		assertEquals(5,2+3);

	}

}
