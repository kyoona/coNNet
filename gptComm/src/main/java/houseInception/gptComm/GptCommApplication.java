package houseInception.gptComm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class GptCommApplication {

	public static void main(String[] args) {
		SpringApplication.run(GptCommApplication.class, args);
	}

}
