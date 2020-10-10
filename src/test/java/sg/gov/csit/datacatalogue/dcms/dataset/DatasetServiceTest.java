package sg.gov.csit.datacatalogue.dcms.dataset;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import sg.gov.csit.datacatalogue.dcms.databaselink.DatabaseActions;
import sg.gov.csit.datacatalogue.dcms.databaselink.GetBean;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetExistsException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class DatasetServiceTest {
    private static List<String> datasetsCreated = new ArrayList<>();

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
    public void createNewDataset_GivenDatasetIdNotInDb_ShouldReturnTrue() throws SQLException {
        // arrange & act
        when(datasetRepository.findByName(anyString())).thenReturn(null);
        datasetsCreated.add("DatasetServiceTest_mockDatabase"); // add to list of datasets to be dropped after this class's tests is done
        // should change it soon
        GetBean.currentMavenProfile = "test";
        GetBean.currentDataBaseDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        GetBean.currentDataBaseUrl = "jdbc:sqlserver://localhost:1433;databaseName=testdb;integratedSecurity=false";
        GetBean.userName = "sa";
        GetBean.password = "Password1";
        // assert
        assertTrue(() -> datasetService.createNewDataset("DatasetServiceTest_mockDatabase", ""));
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
