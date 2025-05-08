package com.codeavenue.code_avenue_map;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@OpenAPIDefinition(info = @Info(
		title = "API توثيق",
		version = "1.0",
		description = "هذا هو API لمشروعك، يمكنك تجربة جميع الطلبات هنا!"
))
public class CodeAvenueMapApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeAvenueMapApplication.class, args);
	}

}
