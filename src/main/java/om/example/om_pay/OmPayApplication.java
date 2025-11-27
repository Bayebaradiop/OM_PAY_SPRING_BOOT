package om.example.om_pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OmPayApplication {

	public static void main(String[] args) {
		SpringApplication.run(OmPayApplication.class, args);
	}

}
