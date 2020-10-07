package sg.gov.csit.datacatalogue.dcms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;

@SpringBootApplication
public class DcMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DcMsApplication.class, args);
	}
}
