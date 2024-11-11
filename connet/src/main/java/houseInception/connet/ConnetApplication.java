package houseInception.connet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ConnetApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConnetApplication.class, args);
	}

}
