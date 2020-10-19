package sg.gov.csit.datacatalogue.dcms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class DcMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DcMsApplication.class, args);
	}

	//https://stackoverflow.com/questions/42874351/spring-boot-enabling-cors-by-application-properties
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("http://localhost:8081");
			}
		};
	}
}
