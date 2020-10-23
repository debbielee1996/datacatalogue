package sg.gov.csit.datacatalogue.dcms.dataset;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;

import sg.gov.csit.datacatalogue.dcms.databaselink.DatabaseActions;
import sg.gov.csit.datacatalogue.dcms.databaselink.GetBean;
import sg.gov.csit.datacatalogue.dcms.datasetaccess.DatasetAccess;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTableService;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetAccessNotFoundException;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetExistsException;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetNotFoundException;
import sg.gov.csit.datacatalogue.dcms.exception.OfficerNotFoundException;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;
import sg.gov.csit.datacatalogue.dcms.officer.OfficerService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class DatasetServiceTest {
    private static List<String> datasetsCreated = new ArrayList<>();

    @Mock
    DatasetRepository datasetRepository;

    @Mock
    OfficerService officerService;

    @InjectMocks
    DatasetService datasetService;


    @Test
    public void createNewDataset_GivenDatasetIdInDb_ShouldThrowException() {
        // arrange & act
        when(datasetRepository.findByName(anyString())).thenReturn(new Dataset());

        // assert
        assertThrows(DatasetExistsException.class, () -> datasetService.createNewDataset("mock", "", "123"));
    }

    @Test
    public void createNewDataset_GivenDatasetIdInDbAndOfficerNotExists_ShouldThrowException() {
        // arrange & act
        when(datasetRepository.findByName(anyString())).thenReturn(null);
        doReturn(Optional.<Officer>empty()).when(officerService).getOfficer(anyString());

        // assert
        assertThrows(OfficerNotFoundException.class, () -> datasetService.createNewDataset("mock", "", "123"));
    }

    @Test
    public void createNewDataset_GivenDatasetIdNotInDb_ShouldReturnTrue() {
        // arrange & act
        when(datasetRepository.findByName(anyString())).thenReturn(null);
        datasetsCreated.add("DatasetServiceTest_mockDatabase"); // add to list of datasets to be dropped after this class's tests is done
        // should change it soon
        GetBean.currentMavenProfile = "test";
        GetBean.currentDataBaseDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        GetBean.currentDataBaseUrl = "jdbc:sqlserver://localhost:1433;databaseName=testdb;integratedSecurity=false";
        GetBean.userName = "sa";
        GetBean.password = "Password1";

        String pf = "123";
        Officer mockOfficer = new Officer(pf,"test","testEmail", "123", "System Admin");

        // act
        doReturn(Optional.of(mockOfficer)).when(officerService).getOfficer(pf);

        // assert
        assertTrue(() -> datasetService.createNewDataset("DatasetServiceTest_mockDatabase", "", pf));
    }


    @Test
    public void ValidateOfficerDatasetAccess_GivenOfficerPfAndOfficerNotInDb_ShouldThrowException(){
        //arrange
        String pf = "123";
        long datasetId = 321;

        // act and assert
        assertThrows(OfficerNotFoundException.class,
                () -> datasetService.ValidateOfficerDatasetAccess(pf,datasetId));
    }

    @Test
    public void ValidateOfficerDatasetAccess_GivenOfficerPfAndDatasetNotInDb_ShouldThrowException(){
        //arrange
        String pf = "123";
        long datasetId = 321;

        // act
        doReturn(true).when(officerService).IsOfficerInDatabase(anyString());
        when(datasetRepository.findById(anyLong())).thenReturn(Optional.<Dataset>empty());

        // assert
        assertThrows(DatasetNotFoundException.class,
                () -> datasetService.ValidateOfficerDatasetAccess(pf,datasetId));
    }

    @Test
    public void ValidateOfficerDatasetAccess_GivenOfficerPfAndNoDatasetAccess_ShouldReturnFalse(){
        // arrange
        String pf = "123";
        Officer mockOfficer = new Officer(pf,"test","testEmail", "123", "System Admin");
        long datasetId = 321;
        Dataset mockDataset = new Dataset("mock", "mock", mockOfficer);

        // act
        doReturn(true).when(officerService).IsOfficerInDatabase(anyString());
        when(datasetRepository.findById(anyLong())).thenReturn(Optional.of(mockDataset));

        // assert
        assertFalse(() -> datasetService.ValidateOfficerDatasetAccess(pf,datasetId));
    }

    @Test
    public void ValidateOfficerDatasetAccess_GivenOfficerPfNotInDatasetAccess_ShouldReturnFalse(){
        // arrange
        // mock officer with Ddcs list
        String pf = "123";
        Officer mockOfficer = new Officer(pf,"test","testEmail", "123", "System Admin");

        // mock dataset with DatasetAccessList
        Dataset mockDataset = new Dataset("mock", "mock", mockOfficer);
        List<DatasetAccess> datasetAccessList = new ArrayList<>();
        datasetAccessList.add(new DatasetAccess(mockDataset, "Pf", "999"));
        mockDataset.setDatasetAccessList(datasetAccessList);

        //act
        doReturn(true).when(officerService).IsOfficerInDatabase(anyString());
        when(datasetRepository.findById(anyLong())).thenReturn(Optional.of(mockDataset));

        // assert
        assertFalse(() -> datasetService.ValidateOfficerDatasetAccess(pf, anyLong()));
    }

    @Test
    public void ValidateOfficerDatasetAccess_GivenOfficerPfAndPfInDatasetAccess_ShouldReturnTrue(){
        // arrange
        // mock officer with Ddcs list
        String pf = "123";
        Officer mockOfficer = new Officer(pf,"test","testEmail", "123", "System Admin");

        // mock dataset with DatasetAccessList
        Dataset mockDataset = new Dataset("mock", "mock", mockOfficer);
        List<DatasetAccess> datasetAccessList = new ArrayList<>();
        datasetAccessList.add(new DatasetAccess(mockDataset, "Pf", "123"));
        mockDataset.setDatasetAccessList(datasetAccessList);

        //act
        doReturn(true).when(officerService).IsOfficerInDatabase(anyString());
        when(datasetRepository.findById(anyLong())).thenReturn(Optional.of(mockDataset));

        // assert
        assertTrue(() -> datasetService.ValidateOfficerDatasetAccess(pf, anyLong()));
    }

    // clean up db with new datasets (databases) created
    @AfterAll
    public static void tearDown() throws SQLException {
        DatabaseActions databaseActions = new DatabaseActions();
        Connection conn = databaseActions.getConnection();
        PreparedStatement ps = null;
        for (String name:datasetsCreated) {
            ps = conn.prepareStatement("DROP DATABASE " + name);
            ps.execute();
        }
        conn.close();
    }
}
