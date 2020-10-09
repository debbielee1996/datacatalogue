package sg.gov.csit.datacatalogue.dcms.dataset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import sg.gov.csit.datacatalogue.dcms.databaselink.GetBean;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetExistsException;

@ExtendWith(MockitoExtension.class)
public class DatasetServiceTest {
    @Mock
    DatasetRepository datasetRepository;

    @InjectMocks
    DatasetService datasetService;

    @Test
    public void createNewDataset_GivenDatasetIdInDb_ShouldThrowException() {
        // arrange & act
        when(datasetRepository.findByName(anyString())).thenReturn(new Dataset());

        // assert
        assertThrows(DatasetExistsException.class, () -> datasetService.createNewDataset("mock", ""));
    }

    @Test
    public void createNewDataset_GivenDatasetIdNotInDb_ShouldReturnTrue() {
        // arrange & act
        when(datasetRepository.findByName(anyString())).thenReturn(null);
        // should change it soon
        GetBean.currentMavenProfile = "test";
        GetBean.currentDataBaseDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        GetBean.currentDataBaseUrl = "jdbc:sqlserver://localhost:1433;databaseName=testdb;integratedSecurity=false";
        GetBean.userName = "sa";
        GetBean.password = "Password1";
        // assert
        assertTrue(() -> datasetService.createNewDataset("mock", ""));
    }
}
