package sg.gov.csit.datacatalogue.dcms.officer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class OfficerServiceTest {

    @Mock
    private OfficerRepository officerRepository;

    @InjectMocks
    private OfficerService officerService;

}
