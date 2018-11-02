package com.cwtsite.cwt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:cwt")
public class CwtApplicationTests {

	@Test
	public void contextLoads() {
	}

}
