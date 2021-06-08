package com.copycoding.demo;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

	@Test
	void contextLoads() {

		File f = new File("a:");
		for (int i = 0; i < f.listFiles().length; i++) {
			System.out.println(f.listFiles()[i].getPath());
		}

	}

}
