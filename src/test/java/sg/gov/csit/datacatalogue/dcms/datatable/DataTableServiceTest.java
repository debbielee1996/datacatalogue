package sg.gov.csit.datacatalogue.dcms.datatable;


import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import sg.gov.csit.datacatalogue.dcms.databaselink.DatabaseActions;
import sg.gov.csit.datacatalogue.dcms.databaselink.GetBean;
import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;
import sg.gov.csit.datacatalogue.dcms.dataset.DatasetService;
import sg.gov.csit.datacatalogue.dcms.datasetaccess.DatasetAccess;
import sg.gov.csit.datacatalogue.dcms.datatable.mock.DataTableStubFactory;
import sg.gov.csit.datacatalogue.dcms.datatableaccess.DataTableAccess;
import sg.gov.csit.datacatalogue.dcms.datatablecolumn.DataTableColumnService;
import sg.gov.csit.datacatalogue.dcms.exception.*;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;
import sg.gov.csit.datacatalogue.dcms.officer.OfficerService;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DataTableServiceTest {
    private static List<String> datatablesCreated = new ArrayList<>();

    @Mock
    DataTableRepository dataTableRepository;

    @Mock
    OfficerService officerService;

    @Mock
    DatasetService datasetService;

    @InjectMocks
    DataTableService dataTableService;

    @BeforeAll
    public static void setUp() throws SQLException {
        // create test db for DataTableSericeTest_dataset1 from DataTableStubFactory
        GetBean.currentMavenProfile = "test";
        GetBean.currentDataBaseDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        GetBean.currentDataBaseUrl = "jdbc:sqlserver://localhost:1433;databaseName=testdb;integratedSecurity=false";
        GetBean.userName = "sa";
        GetBean.password = "Password1";

        DatabaseActions dba = new DatabaseActions();
        Connection conn = dba.getConnection();
        String initDB = "DataTableSericeTest_dataset1";
        PreparedStatement ps = conn.prepareStatement("CREATE DATABASE "+ initDB);
        ps.execute();
        conn.close();
    }

    @Test
    public void uploadFile_GivenOfficerPfNotInDb_ShouldThrowException() {
        // arrange
        MultipartFile file = null;
        String tableName = "mock";
        String datasetId = "1";
        String description = "This is a mock datatable";
        String pf = "123";

        // act
        doReturn(Optional.<Officer>empty()).when(officerService).getOfficer(anyString());

        // assert
        Assertions.assertThrows(OfficerNotFoundException.class, () -> dataTableService.uploadFile(file, tableName, datasetId, description, new ArrayList<>(), pf, new ArrayList<>()));
    }


    @Test
    public void uploadFile_GivenDatasetIdNotInDb_ShouldThrowException() {
        // arrange
        MultipartFile file = null;
        String tableName = "mock";
        String datasetId = "1";
        String description = "This is a mock datatable";
        String pf = "123";
        Officer mockOfficer = DataTableStubFactory.OFFICER();

        // act
        doReturn(Optional.of(mockOfficer)).when(officerService).getOfficer(anyString());

        // assert
        Assertions.assertThrows(DatasetExistsException.class, () -> dataTableService.uploadFile(file, tableName, datasetId, description, new ArrayList<>(), pf, new ArrayList<>()));
    }

    @Test
    public void uploadFile_GivenDatasetInDbAndFileExtIsNotCSVOrExcel_ShouldThrowException() throws IOException {
        // arrange
        // arrange mock .pdf file
        FileInputStream inputFile = DataTableStubFactory.FILESTREAM_PDFFILE();
        MultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", inputFile);
        String tableName = "mock";
        String datasetId = "1";
        String description = "This is a mock datatable";
        String pf = "123";
        Officer mockOfficer = DataTableStubFactory.OFFICER();

        // act
        doReturn(Optional.of(mockOfficer)).when(officerService).getOfficer(anyString());
        when(datasetService.getDatasetById(anyLong())).thenReturn(Optional.of(new Dataset()));
        when(dataTableRepository.findByNameAndDatasetId(anyString(), anyLong())).thenReturn(new DataTable());

        // assert
        Assertions.assertThrows(IncorrectFileTypeException.class, () -> dataTableService.uploadFile(file, tableName, datasetId, description, new ArrayList<>(), pf, new ArrayList<>()));
    }

    @Test
    public void uploadFile_CSV_GivenDatasetIdInDbAndDataTableInDb_ShouldReturnTrue() throws IOException {
        // arrange
        FileInputStream inputFile = DataTableStubFactory.FILESTREAM_CSVFILE();
        List<String> dataColDesc = DataTableStubFactory.DATACOLDESC_CSVFILE();
        List<String> dataTypes = DataTableStubFactory.DATATYPES_CSVFILE();
        MultipartFile file = new MockMultipartFile("file", "test.csv", "application/csv", inputFile);
        String tableName = "DataTableServiceTest_mock1";
        String datasetId = "1";
        String description = "This is a mock datatable";

        Dataset dataset = DataTableStubFactory.DATASET();

        datatablesCreated.add(dataset.getName()+".dbo."+tableName); // add to list of datatables to be dropped after this class's tests is done
        String pf = "123";
        Officer mockOfficer = DataTableStubFactory.OFFICER();

        // act
        doReturn(Optional.of(mockOfficer)).when(officerService).getOfficer(anyString());
        when(datasetService.getDatasetById(anyLong())).thenReturn(Optional.of(dataset));
        when(dataTableRepository.findByNameAndDatasetId(anyString(), anyLong())).thenReturn(new DataTable("mock", "mock", dataset, mockOfficer));

        Assertions.assertTrue(() -> {
            try {
                return dataTableService.uploadFile(file, tableName, datasetId, description, dataTypes, pf, dataColDesc);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (CsvException e) {
                e.printStackTrace();
                return false;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    @Test
    public void uploadFile_CSV_GivenDatasetIdInDbAndDataTableNotInDb_ShouldReturnTrue() throws IOException, SQLException {
        // arrange
        FileInputStream inputFile = DataTableStubFactory.FILESTREAM_CSVFILE();
        List<String> dataColDesc = DataTableStubFactory.DATACOLDESC_CSVFILE();
        List<String> dataTypes = DataTableStubFactory.DATATYPES_CSVFILE();
        MultipartFile file = new MockMultipartFile("file", "test.csv", "application/csv", inputFile);
        String tableName = "DataTableServiceTest_mock2";
        String datasetId = "1";
        String description = "This is a mock datatable";


        Dataset dataset = DataTableStubFactory.DATASET();

        datatablesCreated.add(dataset.getName()+".dbo."+tableName); // add to list of datatables to be dropped after this class's tests is done

        String pf = "123";
        Officer mockOfficer = DataTableStubFactory.OFFICER();

        // act
        doReturn(Optional.of(mockOfficer)).when(officerService).getOfficer(anyString());
        when(datasetService.getDatasetById(anyLong())).thenReturn(Optional.of(dataset));
        when(dataTableRepository.findByNameAndDatasetId(anyString(), anyLong())).thenReturn(null);

        Assertions.assertTrue(() -> {
            try {
                return dataTableService.uploadFile(file, tableName, datasetId, description, dataTypes, pf, dataColDesc);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (CsvException e) {
                e.printStackTrace();
                return false;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    @Test
    public void uploadFile_XLSX_GivenDatasetIdInDbAndDataTableInDb_ShouldReturnTrue() throws IOException {
        // arrange
        FileInputStream inputFile = DataTableStubFactory.FILESTREAM_XLSXFILE();
        List<String> dataColDesc = DataTableStubFactory.DATACOLDESC_XLSXFILE();
        List<String> dataTypes = DataTableStubFactory.DATATYPES_XLSXFILE();
        MultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", inputFile);
        String tableName = "DataTableServiceTest_mock3";
        String datasetId = "1";
        String description = "This is a mock datatable";

        Dataset dataset = DataTableStubFactory.DATASET();

        datatablesCreated.add(dataset.getName()+".dbo."+tableName); // add to list of datatables to be dropped after this class's tests is done

        String pf = "123";
        Officer mockOfficer = DataTableStubFactory.OFFICER();

        // act
        doReturn(Optional.of(mockOfficer)).when(officerService).getOfficer(anyString());
        when(datasetService.getDatasetById(anyLong())).thenReturn(Optional.of(dataset));
        when(dataTableRepository.findByNameAndDatasetId(anyString(), anyLong())).thenReturn(new DataTable("mock", "mock", dataset, mockOfficer));

        Assertions.assertTrue(() -> {
            try {
                return dataTableService.uploadFile(file, tableName, datasetId, description, dataTypes, pf, dataColDesc);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (CsvException e) {
                e.printStackTrace();
                return false;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    @Test
    public void uploadFile_XLSX_GivenDatasetIdInDbAndDataTableNotInDb_ShouldReturnTrue() throws IOException, SQLException {
        // arrange
        FileInputStream inputFile = DataTableStubFactory.FILESTREAM_XLSXFILE();
        List<String> dataColDesc = DataTableStubFactory.DATACOLDESC_XLSXFILE();
        List<String> dataTypes = DataTableStubFactory.DATATYPES_XLSXFILE();
        MultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", inputFile);
        String tableName = "DataTableServiceTest_mock4";
        String datasetId = "1";
        String description = "This is a mock datatable";
        Dataset dataset = DataTableStubFactory.DATASET();

        datatablesCreated.add(dataset.getName()+".dbo."+tableName); // add to list of datatables to be dropped after this class's tests is done

        String pf = "123";
        Officer mockOfficer = DataTableStubFactory.OFFICER();

        // act
        doReturn(Optional.of(mockOfficer)).when(officerService).getOfficer(anyString());
        when(datasetService.getDatasetById(anyLong())).thenReturn(Optional.of(dataset));
        when(dataTableRepository.findByNameAndDatasetId(anyString(), anyLong())).thenReturn(null);

        Assertions.assertTrue(() -> {
            try {
                return dataTableService.uploadFile(file, tableName, datasetId, description, dataTypes, pf, dataColDesc);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (CsvException e) {
                e.printStackTrace();
                return false;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    @Test
    public void ValidateOfficerDataTableAccess_GivenOfficerPfAndOfficerNotInDb_ShouldThrowException(){
        //arrange
        String pf = "123";
        long dataTableId = 321;

        // act and assert
        assertThrows(OfficerNotFoundException.class,
                () -> dataTableService.ValidateOfficerDataTableAccess(pf,dataTableId));
    }

    @Test
    public void ValidateOfficerDataTableAccess_GivenOfficerPfAndDataTableNotInDb_ShouldThrowException(){
        //arrange
        String pf = "123";
        long dataTableId = 321;

        // act
        doReturn(true).when(officerService).IsOfficerInDatabase(anyString());
        when(dataTableRepository.findById(anyLong())).thenReturn(Optional.<DataTable>empty());

        // assert
        assertThrows(DataTableNotFoundException.class,
                () -> dataTableService.ValidateOfficerDataTableAccess(pf,dataTableId));
    }

    @Test
    public void ValidateOfficerDataTableAccess_GivenOfficerPfAndNoDataTableAccess_ShouldReturnFalse(){
        // arrange
        String pf = "123";
        Officer mockOfficer = new Officer(pf,"test","testEmail", "123", "System Admin");
        long dataTableId = 321;
        Dataset mockDataset = new Dataset("mock", "mock", mockOfficer);
        DataTable mockDataTable = new DataTable("mock", "mock", mockDataset, mockOfficer);

        // act
        doReturn(true).when(officerService).IsOfficerInDatabase(anyString());
        when(dataTableRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTable));

        // assert
        assertFalse(() -> dataTableService.ValidateOfficerDataTableAccess(pf,dataTableId));
    }

    @Test
    public void ValidateOfficerDataTableAccess_GivenOfficerPfNotInDataTableAccess_ShouldReturnFalse(){
        // arrange
        // mock officer with Ddcs list
        String pf = "123";
        Officer mockOfficer = new Officer(pf,"test","testEmail", "123", "System Admin");

        // mock dataTable with DataTableAccessList
        Dataset mockDataset = new Dataset("mock", "mock", mockOfficer);
        DataTable mockDataTable = new DataTable("mock", "mock", mockDataset, mockOfficer);

        List<DataTableAccess> dataTableAccessList = new ArrayList<>();
        dataTableAccessList.add(new DataTableAccess(mockDataTable, "Pf", "999"));
        mockDataTable.setDataTableAccessList(dataTableAccessList);

        //act
        doReturn(true).when(officerService).IsOfficerInDatabase(anyString());
        when(dataTableRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTable));

        // assert
        assertFalse(() -> dataTableService.ValidateOfficerDataTableAccess(pf, anyLong()));
    }

    @Test
    public void ValidateOfficerDataTableAccess_GivenOfficerPfAndPfInDataTableAccess_ShouldReturnTrue(){
        // arrange
        // mock officer with Ddcs list
        String pf = "123";
        Officer mockOfficer = new Officer(pf,"test","testEmail", "123", "System Admin");

        // mock dataTable with DataTableAccessList
        Dataset mockDataset = new Dataset("mock", "mock", mockOfficer);
        DataTable mockDataTable = new DataTable("mock", "mock", mockDataset, mockOfficer);

        List<DataTableAccess> dataTableAccessList = new ArrayList<>();
        dataTableAccessList.add(new DataTableAccess(mockDataTable, "Pf", "123"));
        mockDataTable.setDataTableAccessList(dataTableAccessList);

        //act
        doReturn(true).when(officerService).IsOfficerInDatabase(anyString());
        when(dataTableRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTable));

        // assert
        assertTrue(() -> dataTableService.ValidateOfficerDataTableAccess(pf, anyLong()));
    }

    // clean up db with new datatables (tables) created
    @AfterAll
    public static void tearDown() throws SQLException {
        DatabaseActions databaseActions = new DatabaseActions();
        Connection conn = databaseActions.getConnection();
        PreparedStatement ps = null;
        for (String name:datatablesCreated) {
            ps = conn.prepareStatement("DROP TABLE " + name);
            ps.execute();
        }
        String initDB = "DataTableSericeTest_dataset1";
        ps = conn.prepareStatement("DROP DATABASE "+initDB);
        ps.execute();

        conn.close();
    }
}
