package sg.gov.csit.datacatalogue.dcms.datatable;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import sg.gov.csit.datacatalogue.dcms.databaselink.GetBean;
import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;
import sg.gov.csit.datacatalogue.dcms.dataset.DatasetService;
import sg.gov.csit.datacatalogue.dcms.datatable.mock.DataTableStubFactory;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetExistsException;
import sg.gov.csit.datacatalogue.dcms.exception.IncorrectFileTypeException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DataTableServiceTest {
    @Mock
    DataTableRepository dataTableRepository;

    @Mock
    DatasetService datasetService;

    @InjectMocks
    DataTableService dataTableService;

    @Mock
    GetBean getBean;

    @Test
    public void uploadFile_GivenDatasetIdNotInDb_ShouldThrowException() {
        MultipartFile file = null;
        String tableName = "mock";
        String datasetId = "1";
        String description = "This is a mock datatable";

        Assertions.assertThrows(DatasetExistsException.class, () -> dataTableService.uploadFile(file, tableName, datasetId, description));
    }

    @Test
    public void uploadFile_GivenDatasetInDbAndFileExtIsNotCSV_ShouldThrowException() throws IOException {
        // arrange
        // arrange mock .pdf file
        FileInputStream inputFile = DataTableStubFactory.FILESTREAM_PDFFILE();
        MultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", inputFile);
        String tableName = "mock";
        String datasetId = "1";
        String description = "This is a mock datatable";

        // act
        when(datasetService.getDatasetById(anyLong())).thenReturn(Optional.of(new Dataset()));
        when(dataTableRepository.findByName(anyString())).thenReturn(new DataTable());

        // assert
        Assertions.assertThrows(IncorrectFileTypeException.class, () -> dataTableService.uploadFile(file, tableName, datasetId, description));
    }

    @Test
    public void uploadFile_GivenDatasetIdInDbAndDataTableInDb_ShouldReturnTrue() throws IOException {
        // arrange
        FileInputStream inputFile = DataTableStubFactory.FILESTREAM_CSVFILE();
        MultipartFile file = new MockMultipartFile("file", "test.csv", "application/csv", inputFile);
        String tableName = "mock";
        String datasetId = "1";
        String description = "This is a mock datatable";

        Dataset dataset = DataTableStubFactory.DATASET();

        // should change it soon
        GetBean.currentMavenProfile = "test";
        GetBean.currentDataBaseDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        GetBean.currentDataBaseUrl = "jdbc:sqlserver://localhost:1433;databaseName=testdb;integratedSecurity=false";
        GetBean.userName = "sa";
        GetBean.password = "Password1";

        // act
        when(datasetService.getDatasetById(anyLong())).thenReturn(Optional.of(dataset));
        when(dataTableRepository.findByName(anyString())).thenReturn(new DataTable());

        Assertions.assertTrue(() -> dataTableService.uploadFile(file, tableName, datasetId, description));
    }

    @Test
    public void uploadFile_GivenDatasetIdInDbAndDataTableNotInDb_ShouldReturnTrue() throws IOException {
        // arrange
        FileInputStream inputFile = DataTableStubFactory.FILESTREAM_CSVFILE();
        MultipartFile file = new MockMultipartFile("file", "test.csv", "application/csv", inputFile);
        String tableName = "mock2";
        String datasetId = "1";
        String description = "This is a mock datatable";

        Dataset dataset = DataTableStubFactory.DATASET();

        // should change it soon
        GetBean.currentMavenProfile = "test";
        GetBean.currentDataBaseDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        GetBean.currentDataBaseUrl = "jdbc:sqlserver://localhost:1433;databaseName=testdb;integratedSecurity=false";
        GetBean.userName = "sa";
        GetBean.password = "Password1";

        // act
        when(datasetService.getDatasetById(anyLong())).thenReturn(Optional.of(dataset));
        when(dataTableRepository.findByName(anyString())).thenReturn(null);

        Assertions.assertTrue(() -> dataTableService.uploadFile(file, tableName, datasetId, description));
    }
}
