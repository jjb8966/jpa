package jpabook.jpashopproject;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopProjectApplication.class, args);
	}

	// 프록시 객체는 API 응답으로 보내지 않도록 하기위해 등록
	@Bean
	public Hibernate5Module hibernate5Module() {
		return new Hibernate5Module();
	}
}
