package sg.gov.csit.datacatalogue.dcms;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import sg.gov.csit.datacatalogue.dcms.datasetaccess.DatasetAccessRepository;
import sg.gov.csit.datacatalogue.dcms.officer.OfficerRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestApiIntegrationTest {
    private final String testUrl = "http://localhost:";

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    //Repos
    @Autowired
    private OfficerRepository officerRepository;

    @Autowired
    private DatasetAccessRepository datasetAccessRepository;

    @Test
    public void ValidateOfficerDatasetAccess_ShouldReturnStatusOk(){

    }
}
