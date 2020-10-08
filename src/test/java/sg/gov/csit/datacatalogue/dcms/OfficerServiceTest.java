package sg.gov.csit.datacatalogue.dcms;

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
import sg.gov.csit.datacatalogue.dcms.officer.Officer;
import sg.gov.csit.datacatalogue.dcms.officer.OfficerRepository;
import sg.gov.csit.datacatalogue.dcms.officer.OfficerService;

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

    @Mock
    private DatasetService datasetService;

    @InjectMocks
    private OfficerService officerService;

    @Test
    public void ValidateOfficerDatasetAccess_GivenOfficerPfAndOfficerNotInDb_ShouldThrowException(){
        //arrange
        String pf = "123";
        long datasetId = 321;

        // act and assert
        assertThrows(OfficerNotFoundException.class,
                () -> officerService.ValidateOfficerDatasetAccess(pf,datasetId));
    }

    @Test
    public void ValidateOfficerDatasetAccess_GivenOfficerPfAndDatasetNotInDb_ShouldThrowException(){
        //arrange
        String pf = "123";
        Officer mockOfficer = new Officer(pf,"test","testEmail", "System Admin");
        when(officerRepository.findById(pf)).thenReturn(Optional.of(mockOfficer));

        long datasetId = 321;

        // act and assert
        assertThrows(DatasetNotFoundException.class,
                () -> officerService.ValidateOfficerDatasetAccess(pf,datasetId));
    }

    @Test
    public void ValidateOfficerDatasetAccess_GivenOfficerPfAndNoDatasetAccess_ShouldThrowException(){
        // arrange
        String pf = "123";
        Officer mockOfficer = new Officer(pf,"test","testEmail", "System Admin");
        when(officerRepository.findById(pf)).thenReturn(Optional.of(mockOfficer));

        long datasetId = 321;
        doReturn(true).when(datasetService).IsDatasetInDatabase(anyLong());
        doReturn(Optional.of(new Dataset("mock", "mock"))).when(datasetService).getDatasetById(anyLong());

        // act and assert
        assertThrows(DatasetAccessNotFoundException.class,
                () -> officerService.ValidateOfficerDatasetAccess(pf,datasetId));
    }

    @Test
    public void ValidateOfficerDatasetAccess_GivenOfficerPfNotInDatasetAccess_ShouldThrowException(){
        // arrange
        // mock officer with Ddcs list
        String pf = "123";
        Officer mockOfficer = new Officer(pf,"test","testEmail", "System Admin");
        List<Ddcs> ddcsList = new ArrayList<>();
        ddcsList.add(new Ddcs("CSIT","IT","ES","FPS"));
        mockOfficer.setDdcsList(ddcsList);

        // mock dataset with DatasetAccessList
        Dataset dataset = new Dataset("mock", "mock");
        List<DatasetAccess> datasetAccessList = new ArrayList<>();
        datasetAccessList.add(new DatasetAccess(dataset, "Pf", "999"));
        dataset.setDatasetAccessList(datasetAccessList);

        //act
        when(officerRepository.findById(pf)).thenReturn(Optional.of(mockOfficer));
        doReturn(true).when(datasetService).IsDatasetInDatabase(anyLong());
        doReturn(Optional.of(dataset)).when(datasetService).getDatasetById(anyLong());

        // assert
        assertThrows(DatasetAccessNotFoundException.class,
                () -> officerService.ValidateOfficerDatasetAccess(pf, anyLong()));
    }

    @Test
    public void ValidateOfficerDatasetAccess_GivenOfficerDdcsNotInDatasetAccess_ShouldThrowException(){
        // arrange
        // mock officer with Ddcs list
        String pf = "123";
        Officer mockOfficer = new Officer(pf,"test","testEmail", "System Admin");
        Ddcs ddcs = new Ddcs("CSIT","IT","ES","FPS");
        Ddcs ddcs2 = new Ddcs("CSIT","IT","ES","HCS"); // mock second Ddcs
        ddcs.setId(1);
        ddcs2.setId(2);

        List<Ddcs> ddcsList = new ArrayList<>();
        ddcsList.add(ddcs);
        mockOfficer.setDdcsList(ddcsList);

        // mock dataset with DatasetAccessList
        Dataset dataset = new Dataset("mock", "mock");
        List<DatasetAccess> datasetAccessList = new ArrayList<>();
        // dataset has access for ddcs2 instead
        datasetAccessList.add(new DatasetAccess(dataset, "Ddcs", "2"));
        dataset.setDatasetAccessList(datasetAccessList);

        //act
        when(officerRepository.findById(pf)).thenReturn(Optional.of(mockOfficer));
        doReturn(true).when(datasetService).IsDatasetInDatabase(anyLong());
        doReturn(Optional.of(dataset)).when(datasetService).getDatasetById(anyLong());

        // assert
        assertThrows(DatasetAccessNotFoundException.class,
                () -> officerService.ValidateOfficerDatasetAccess(pf, anyLong()));
    }

    @Test
    public void ValidateOfficerDatasetAccess_GivenOfficerPfAndPfInDatasetAccess_ShouldReturnTrue(){
        // arrange
        // mock officer with Ddcs list
        String pf = "123";
        Officer mockOfficer = new Officer(pf,"test","testEmail", "System Admin");
        List<Ddcs> ddcsList = new ArrayList<>();
        ddcsList.add(new Ddcs("CSIT","IT","ES","FPS"));
        mockOfficer.setDdcsList(ddcsList);

        // mock dataset with DatasetAccessList
        Dataset dataset = new Dataset("mock", "mock");
        List<DatasetAccess> datasetAccessList = new ArrayList<>();
        datasetAccessList.add(new DatasetAccess(dataset, "Pf", "123"));
        dataset.setDatasetAccessList(datasetAccessList);

        //act
        when(officerRepository.findById(pf)).thenReturn(Optional.of(mockOfficer));
        doReturn(true).when(datasetService).IsDatasetInDatabase(anyLong());
        doReturn(Optional.of(dataset)).when(datasetService).getDatasetById(anyLong());

        // assert
        assertTrue(() -> officerService.ValidateOfficerDatasetAccess(pf, anyLong()));
    }

    @Test
    public void ValidateOfficerDatasetAccess_GivenOfficerPfAndDdcsInDatasetAccess_ShouldReturnTrue(){
        // arrange
        // mock officer with Ddcs list
        String pf = "123";
        Officer mockOfficer = new Officer(pf,"test","testEmail", "System Admin");
        Ddcs ddcs = new Ddcs("CSIT","IT","ES","FPS");
        ddcs.setId(1);

        List<Ddcs> ddcsList = new ArrayList<>();
        ddcsList.add(ddcs);
        mockOfficer.setDdcsList(ddcsList);

        // mock dataset with DatasetAccessList
        Dataset dataset = new Dataset("mock", "mock");
        List<DatasetAccess> datasetAccessList = new ArrayList<>();
        datasetAccessList.add(new DatasetAccess(dataset, "Ddcs", "1"));
        dataset.setDatasetAccessList(datasetAccessList);

        //act
        when(officerRepository.findById(pf)).thenReturn(Optional.of(mockOfficer));
        doReturn(true).when(datasetService).IsDatasetInDatabase(anyLong());
        doReturn(Optional.of(dataset)).when(datasetService).getDatasetById(anyLong());

        // assert
        assertTrue(() -> officerService.ValidateOfficerDatasetAccess(pf, anyLong()));
    }
}
