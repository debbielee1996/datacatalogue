package sg.gov.csit.datacatalogue.dcms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;
import sg.gov.csit.datacatalogue.dcms.dataset.DatasetRepository;
import sg.gov.csit.datacatalogue.dcms.dataset.DatasetService;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetAccessNotFoundException;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetNotFoundException;
import sg.gov.csit.datacatalogue.dcms.exception.OfficerNotFoundException;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;
import sg.gov.csit.datacatalogue.dcms.officer.OfficerRepository;
import sg.gov.csit.datacatalogue.dcms.officer.OfficerService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OfficerServiceTest {

    @Mock
    private OfficerRepository officerRepository;

    @Mock
    private DatasetRepository datasetRepository;

    @Mock
    private DatasetService datasetService;

    @InjectMocks
    private OfficerService officerService;

    @Test
    public void ValidateOfficerDatasetAccess_GivenOfficerPfAndOfficerNotInDb_ShouldThrowException(){
        //arrange
        String pf = "123";
        long datasetId = 321;

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

        assertThrows(DatasetNotFoundException.class,
                () -> officerService.ValidateOfficerDatasetAccess(pf,datasetId));
    }

    @Test
    public void ValidateOfficerDatasetAccess_GivenOfficerPfAndNoDatasetAccess_ShouldThrowException(){
        // arrange
//        String pf = "123";
//        Officer mockOfficer = new Officer(pf, "test", "testEmail", null, null);
//        when(officerRepository.findById(pf)).thenReturn(Optional.of(mockOfficer));
//
//        long datasetId = 321;
////        Dataset mockDataset = new Dataset(datasetId, "testDataset", "This is a test", null);
////        when(datasetRepository.findById(datasetId)).thenReturn(Optional.of(mockDataset));
//
//        Mockito.doReturn(true).when(datasetService).IsDatasetInDatabase(datasetId);
//
//        assertThrows(DatasetAccessNotFoundException.class,
//                () -> officerService.ValidateOfficerDatasetAccess(pf,datasetId));
    }

    @Test
    public void ValidateOfficerDatasetAccess_GivenOfficerPfNotInDatasetAccess_ShouldThrowException(){

    }

    @Test
    public void ValidateOfficerDatasetAccess_GivenOfficerDdcsNotInDatasetAccess_ShouldThrowException(){

    }

    @Test
    public void ValidateOfficerDatasetAccess_GivenOfficerPfAndInDatasetAccess_ShouldReturnTrue(){

    }

}
