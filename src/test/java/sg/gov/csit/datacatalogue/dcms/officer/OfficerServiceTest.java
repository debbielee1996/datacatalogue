package sg.gov.csit.datacatalogue.dcms.officer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;
import sg.gov.csit.datacatalogue.dcms.dataset.DatasetService;
import sg.gov.csit.datacatalogue.dcms.datasetaccess.DatasetAccess;
import sg.gov.csit.datacatalogue.dcms.ddcs.Ddcs;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetAccessNotFoundException;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetNotFoundException;
import sg.gov.csit.datacatalogue.dcms.exception.OfficerNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OfficerServiceTest {

    @Mock
    private OfficerRepository officerRepository;

    @InjectMocks
    private OfficerService officerService;

}
