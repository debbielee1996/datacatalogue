package sg.gov.csit.datacatalogue.dcms.datatable;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sg.gov.csit.datacatalogue.dcms.dataset.DatasetService;

@ExtendWith(MockitoExtension.class)
public class DataTableServiceTest {
    @Mock
    DataTableRepository dataTableRepository;

    @Mock
    DatasetService datasetService;

    @InjectMocks
    DataTableService dataTableService;

    @Test
    public void uploadFile_GivenDatasetIdNotInDb_ShouldThrowException() {

    }

    @Test
    public void uploadFile_GivenDatasetInDbAndFileExtIsNotCSV_ShouldThrowException() {

    }

    @Test
    public void uploadFile_GivenDatasetIdInDbAndDataTableNotInDb_ShouldReturnTrue() {

    }

    @Test
    public void uploadFile_GivenDatasetIdInDbAndDataTableInDb_ShouldReturnTrue() {

    }
}
