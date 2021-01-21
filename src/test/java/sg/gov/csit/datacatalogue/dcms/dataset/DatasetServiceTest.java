package sg.gov.csit.datacatalogue.dcms.dataset;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
import sg.gov.csit.datacatalogue.dcms.dataset.mock.DatasetStubFactory;
import sg.gov.csit.datacatalogue.dcms.datasetaccess.DatasetAccess;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;
import sg.gov.csit.datacatalogue.dcms.datatable.mock.DataTableStubFactory;
import sg.gov.csit.datacatalogue.dcms.exception.*;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;
import sg.gov.csit.datacatalogue.dcms.officer.OfficerRepository;

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
    OfficerRepository officerRepository;

    @InjectMocks
    DatasetService datasetService;

    @BeforeAll
    public static void setUp() throws SQLException {
        // create test db for DataTableSericeTest_dataset1 from DataTableStubFactory
        GetBean.currentMavenProfile = "test";
        GetBean.currentDataBaseDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        GetBean.currentDataBaseUrl = "jdbc:sqlserver://localhost:1433;databaseName=testdb;integratedSecurity=false";
        GetBean.userName = "sa";
        GetBean.password = "Password1";
        GetBean.maximumPoolSize = 10;
    }

    @Test
    public void createNewDataset_GivenDatasetIdInDb_ShouldThrowException() {
        // arrange & act
        when(datasetRepository.findByName(anyString())).thenReturn(new Dataset());

        // assert
        assertThrows(DatasetExistsException.class, () -> datasetService.createNewDataset("mock", "", "123", new ArrayList<>(), "123", false));
    }

    @Test
    public void createNewDataset_GivenDatasetIdInDbAndOfficerNotExists_ShouldThrowException() {
        // arrange & act
        when(datasetRepository.findByName(anyString())).thenReturn(null);
        doReturn(Optional.<Officer>empty()).when(officerRepository).findByPf(anyString());

        // assert
        assertThrows(OfficerNotFoundException.class, () -> datasetService.createNewDataset("mock", "", "123", new ArrayList<>(), "123", false));
    }

    @Test
    public void createNewDataset_GivenDatasetIdInDbAndCustodianOfficerNotExists_ShouldThrowException() {
        // arrange
        List<String> custodianPfs = new ArrayList<>();
        custodianPfs.add("456");

        // act
        when(datasetRepository.findByName(anyString())).thenReturn(null);
        doReturn(Optional.<Officer>empty()).when(officerRepository).findByPf(anyString());

        // assert
        assertThrows(OfficerNotFoundException.class, () -> datasetService.createNewDataset("mock", "", "123", custodianPfs, "123", false));
    }

    @Test
    public void createNewDataset_GivenDatasetIdNotInDb_ShouldReturnTrue() {
        // arrange & act
        when(datasetRepository.findByName(anyString())).thenReturn(null);
        datasetsCreated.add("DatasetServiceTest_mockDatabase"); // add to list of datasets to be dropped after this class's tests is done

        Officer mockOfficer = DatasetStubFactory.MOCK_OFFICER();
        String pf = mockOfficer.getPf();

        // act
        doReturn(Optional.of(mockOfficer)).when(officerRepository).findByPf(pf);

        // assert
        assertTrue(() -> datasetService.createNewDataset("DatasetServiceTest_mockDatabase", "", pf, new ArrayList<>(), "123", false));
    }


    @Test
    public void ValidateOfficerDatasetAccess_GivenOfficerPfAndOfficerNotInDb_ShouldThrowException(){
        //arrange
        String pf = "123";
        long datasetId = 123;

        // act and assert
        assertThrows(OfficerNotFoundException.class,
                () -> datasetService.ValidateOfficerDatasetAccess(pf,datasetId));
    }

    @Test
    public void ValidateOfficerDatasetAccess_GivenOfficerPfAndDatasetNotInDb_ShouldThrowException(){
        //arrange
        String pf = "123";
        long datasetId = 123;
        Officer mockOfficer = DatasetStubFactory.MOCK_OFFICER();

        // act
        doReturn(Optional.of(mockOfficer)).when(officerRepository).findByPf(anyString());
        when(datasetRepository.findById(anyLong())).thenReturn(Optional.<Dataset>empty());

        // assert
        assertThrows(DatasetNotFoundException.class,
                () -> datasetService.ValidateOfficerDatasetAccess(pf,datasetId));
    }

    @Test
    public void ValidateOfficerDatasetAccess_GivenOfficerPfAndNoDatasetAccess_ShouldReturnFalse(){
        // arrange
        Officer mockOfficer = DatasetStubFactory.MOCK_OFFICER();
        String pf = mockOfficer.getPf();
        Dataset mockDataset = DatasetStubFactory.MOCK_DATASET_NOACCESSLIST();
        long datasetId = mockDataset.getId();

        // act
        doReturn(Optional.of(mockOfficer)).when(officerRepository).findByPf(anyString());
        when(datasetRepository.findById(anyLong())).thenReturn(Optional.of(mockDataset));

        // assert
        assertFalse(() -> datasetService.ValidateOfficerDatasetAccess(pf,datasetId));
    }

    @Test
    public void ValidateOfficerDatasetAccess_GivenOfficerPfNotInDatasetAccess_ShouldReturnFalse(){
        // arrange
        // mock officer with Ddcs list
        Officer mockOfficer = DatasetStubFactory.MOCK_OFFICER();
        String pf = mockOfficer.getPf();

        // mock dataset with DatasetAccessList
        Dataset mockDataset = DatasetStubFactory.MOCK_DATASET_NOACCESSLIST();
        List<DatasetAccess> datasetAccessList = new ArrayList<>();
        datasetAccessList.add(new DatasetAccess(mockDataset, "Pf", "999"));
        mockDataset.setDatasetAccessList(datasetAccessList);

        //act
        doReturn(Optional.of(mockOfficer)).when(officerRepository).findByPf(anyString());
        when(datasetRepository.findById(anyLong())).thenReturn(Optional.of(mockDataset));

        // assert
        assertFalse(() -> datasetService.ValidateOfficerDatasetAccess(pf, anyLong()));

        // clear datasetAccessList
        mockDataset.getDatasetAccessList().clear();
    }

    @Test
    public void ValidateOfficerDatasetAccess_GivenOfficerPfAndPfInDatasetAccess_ShouldReturnTrue(){
        // arrange
        // mock officer with Ddcs list
        Officer mockOfficer = DatasetStubFactory.MOCK_OFFICER();
        String pf = mockOfficer.getPf();

        // mock dataset with DatasetAccessList
        Dataset mockDataset = DatasetStubFactory.MOCK_DATASET_NOACCESSLIST();
        List<DatasetAccess> datasetAccessList = new ArrayList<>();
        datasetAccessList.add(new DatasetAccess(mockDataset, "Pf", "123"));
        mockDataset.setDatasetAccessList(datasetAccessList);

        //act
        doReturn(Optional.of(mockOfficer)).when(officerRepository).findByPf(anyString());
        when(datasetRepository.findById(anyLong())).thenReturn(Optional.of(mockDataset));

        // assert
        assertTrue(() -> datasetService.ValidateOfficerDatasetAccess(pf, anyLong()));

        // clear datasetAccessList
        mockDataset.getDatasetAccessList().clear();
    }

    @Test
    public void addOfficerDatasetAccess_DatasetNotPresent_ShouldThrowException() {
        // arrange
        when(datasetRepository.findById(anyLong())).thenReturn(Optional.empty());

        // assert
        assertThrows(DatasetNotFoundException.class, () -> datasetService.addOfficerDatasetAccess("123", "123"));
    }

    @Test
    public void addOfficerDatasetAccess_DatasetPresentAndOfficerPFInDatasetAccessList_ShouldReturnTrue() {
        // arrange
        Dataset mockDataset = DatasetStubFactory.MOCK_DATASET_NOACCESSLIST();
        List<DatasetAccess> datasetAccessList = new ArrayList<>();
        datasetAccessList.add(new DatasetAccess(mockDataset, "Pf", "123"));
        mockDataset.setDatasetAccessList(datasetAccessList);

        // act
        when(datasetRepository.findById(anyLong())).thenReturn(Optional.of(mockDataset));

        // assert
        assertTrue(datasetService.addOfficerDatasetAccess("123", "123"));

        // clear datasetAccessList
        mockDataset.getDatasetAccessList().clear();
    }

    @Test
    public void addOfficerDatasetAccess_DatasetPresentAndOfficerPFNotInDatasetAccessList_ShouldReturnTrue() {
        // arrange
        Dataset mockDataset = DatasetStubFactory.MOCK_DATASET_NOACCESSLIST();

        // act
        when(datasetRepository.findById(anyLong())).thenReturn(Optional.of(mockDataset));

        // assert
        assertTrue(datasetService.addOfficerDatasetAccess("123", "123"));
    }

    @Test
    public void removeOfficerDatasetAccess_DatasetNotPresent_ShouldThrowException() {
        // arrange
        when(datasetRepository.findById(anyLong())).thenReturn(Optional.empty());

        // assert
        assertThrows(DatasetNotFoundException.class, () -> datasetService.removeOfficerDatasetAccess("123", "123"));
    }

    @Test
    public void removeOfficerDatasetAccess_DatasetPresentAndOfficerPFInDatasetAccessList_ShouldReturnTrue() {
        // arrange
        Dataset mockDataset = DatasetStubFactory.MOCK_DATASET_NOACCESSLIST();
        List<DatasetAccess> datasetAccessList = new ArrayList<>();
        datasetAccessList.add(new DatasetAccess(mockDataset, "Pf", "123"));
        mockDataset.setDatasetAccessList(datasetAccessList);

        // act
        when(datasetRepository.findById(anyLong())).thenReturn(Optional.of(mockDataset));

        // assert
        assertTrue(datasetService.removeOfficerDatasetAccess("123", "123"));

        // clear datasetAccessList
        mockDataset.getDatasetAccessList().clear();
    }

    @Test
    public void removeOfficerDatasetAccess_DatasetPresentAndOfficerPFNotInDatasetAccessList_ShouldReturnTrue() {
        // arrange
        Dataset mockDataset = DatasetStubFactory.MOCK_DATASET_NOACCESSLIST();

        // act
        when(datasetRepository.findById(anyLong())).thenReturn(Optional.of(mockDataset));

        // assert
        assertTrue(datasetService.removeOfficerDatasetAccess("123", "123"));
    }

    //    test case for access level dataset
    @Test
    public void editDataSetPrivacy_DatasetDoesNotExist_ShouldThrowException() {
        // assert
        assertThrows(DatasetNotFoundException.class, () -> datasetService.editDataSetPrivacy(true, anyLong(), "123"));
    }

    @Test
    public void editDataSetPrivacy_OfficerDoesNotExist_ShouldThrowException() {
        // arrange
        Dataset mockDataset = DatasetStubFactory.MOCK_DATASET_NOACCESSLIST();

        // act
        when(datasetRepository.findById(anyLong())).thenReturn(Optional.of(mockDataset));

        // assert
        assertThrows(OfficerNotFoundException.class, () -> datasetService.editDataSetPrivacy(true, anyLong(), "123"));
    }

    @Test
    public void editDataSetPrivacy_OfficerNotCustodianOrOwner_ShouldThrowException() {
        // arrange
        Dataset mockDataset = DatasetStubFactory.MOCK_DATASET_NOACCESSLIST();
        Officer mockOfficer2 = DatasetStubFactory.MOCK_OFFICER2();

        // act
        when(datasetRepository.findById(anyLong())).thenReturn(Optional.of(mockDataset));
        when(officerRepository.findByPf(anyString())).thenReturn(Optional.of(mockOfficer2));

        // assert
        assertThrows(DatasetAccessNotFoundException.class, () -> datasetService.editDataSetPrivacy(true, Long.parseLong("123"), "456"));
    }

    @Test
    public void editDataSetPrivacy_isPublicDatasetExistAndOfficerIsOwner_ShouldReturnTrue() {
        // arrange
        Dataset mockDataset = DatasetStubFactory.MOCK_DATASET_NOACCESSLIST();
        Officer mockOfficer = DatasetStubFactory.MOCK_OFFICER();

        // act
        when(datasetRepository.findById(anyLong())).thenReturn(Optional.of(mockDataset));
        when(officerRepository.findByPf(anyString())).thenReturn(Optional.of(mockOfficer));

        // assert
        assertTrue(() -> datasetService.editDataSetPrivacy(true, Long.parseLong("123"), "123"));
    }

    @Test
    public void editDatasetPrivacy_isPublicDatasetExistAndOfficerIsCustodian_ShouldReturnTrue() {
        // arrange
        Dataset mockDataset = DatasetStubFactory.MOCK_DATASET_NOACCESSLIST();
        Officer mockOfficer2 = DatasetStubFactory.MOCK_OFFICER2();
        mockDataset.getOfficerCustodianList().add(mockOfficer2); // add mockOfficer2 temporarily as custodian

        // act
        when(datasetRepository.findById(anyLong())).thenReturn(Optional.of(mockDataset));
        when(officerRepository.findByPf(anyString())).thenReturn(Optional.of(mockOfficer2));

        // assert
        assertTrue(() -> datasetService.editDataSetPrivacy(true, Long.parseLong("123"), "456"));

        // empty custodian list
        mockDataset.getOfficerCustodianList().remove(mockOfficer2);
    }

    @Test
    public void editDataSetPrivacy_isPrivateDatasetExistAndOfficerIsOwner_ShouldReturnTrue() {
        // arrange
        Dataset mockDataset = DatasetStubFactory.MOCK_DATASET_NOACCESSLIST();
        Officer mockOfficer = DatasetStubFactory.MOCK_OFFICER();

        // act
        when(datasetRepository.findById(anyLong())).thenReturn(Optional.of(mockDataset));
        when(officerRepository.findByPf(anyString())).thenReturn(Optional.of(mockOfficer));

        // assert
        assertTrue(() -> datasetService.editDataSetPrivacy(false, Long.parseLong("123"), "123"));
    }

    @Test
    public void editDatasetPrivacy_isPrivateDatasetExistAndOfficerIsCustodian_ShouldReturnTrue() {
        // arrange
        Dataset mockDataset = DatasetStubFactory.MOCK_DATASET_NOACCESSLIST();
        Officer mockOfficer2 = DatasetStubFactory.MOCK_OFFICER2();
        mockDataset.getOfficerCustodianList().add(mockOfficer2); // add mockOfficer2 temporarily as custodian

        // act
        when(datasetRepository.findById(anyLong())).thenReturn(Optional.of(mockDataset));
        when(officerRepository.findByPf(anyString())).thenReturn(Optional.of(mockOfficer2));

        // assert
        assertTrue(() -> datasetService.editDataSetPrivacy(false, Long.parseLong("123"), "456"));

        // empty custodian list
        mockDataset.getOfficerCustodianList().remove(mockOfficer2);
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
