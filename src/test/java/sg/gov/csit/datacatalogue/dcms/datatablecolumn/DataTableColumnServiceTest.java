package sg.gov.csit.datacatalogue.dcms.datatablecolumn;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import sg.gov.csit.datacatalogue.dcms.dataset.DatasetRepository;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTableRepository;
import sg.gov.csit.datacatalogue.dcms.datatablecolumn.mock.DataTableColumnStubFactory;
import sg.gov.csit.datacatalogue.dcms.datatablecolumnaccess.DataTableColumnAccess;
import sg.gov.csit.datacatalogue.dcms.exception.DataTableColumnNotFoundException;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetAccessNotFoundException;
import sg.gov.csit.datacatalogue.dcms.exception.OfficerNotFoundException;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;
import sg.gov.csit.datacatalogue.dcms.officer.OfficerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DataTableColumnServiceTest {
    @Mock
    DataTableColumnRepository dataTableColumnRepository;

    @Mock
    OfficerRepository officerRepository;

    @Mock
    DataTableRepository dataTableRepository;

    @Mock
    DatasetRepository datasetRepository;

    @InjectMocks
    DataTableColumnService dataTableColumnService;

    @Test
    public void ValidateOfficerDataTableColumnAccess_GivenOfficerPfAndOfficerNotInDb_ShouldThrowException(){
        //arrange
        String pf = "123";
        long dataTableColumnId = 123;

        // act and assert
        assertThrows(OfficerNotFoundException.class,
                () -> dataTableColumnService.ValidateOfficerDataTableColumnAccess(pf,dataTableColumnId));
    }

    @Test
    public void ValidateOfficerDataTableColumnAccess_GivenOfficerPfAndDataTableColumnNotInDb_ShouldThrowException(){
        //arrange
        String pf = "123";
        long dataTableColumnId = 123;
        Officer mockOfficer = DataTableColumnStubFactory.MOCK_OFFICER();

        // act
        doReturn(Optional.of(mockOfficer)).when(officerRepository).findByPf(anyString());
        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.<DataTableColumn>empty());

        // assert
        assertThrows(DataTableColumnNotFoundException.class,
                () -> dataTableColumnService.ValidateOfficerDataTableColumnAccess(pf,dataTableColumnId));
    }

    @Test
    public void ValidateOfficerDataTableColumnAccess_GivenOfficerPfAndNoDataTableColumnAccess_ShouldReturnFalse(){
        // arrange
        Officer mockOfficer = DataTableColumnStubFactory.MOCK_OFFICER();
        String pf = mockOfficer.getPf();

        DataTableColumn mockDataTableColumn = DataTableColumnStubFactory.MOCK_DATATABLECOLUMN_NOACCESSLIST();
        long dataTableColumnId = mockDataTableColumn.getId();

        // act
        doReturn(Optional.of(mockOfficer)).when(officerRepository).findByPf(anyString());
        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTableColumn));

        // assert
        assertFalse(() -> dataTableColumnService.ValidateOfficerDataTableColumnAccess(pf,dataTableColumnId));
    }

    @Test
    public void ValidateOfficerDataTableColumnAccess_GivenOfficerPfNotInDataTableColumnAccess_ShouldReturnFalse(){
        // arrange
        // mock officer with Ddcs list
        Officer mockOfficer = DataTableColumnStubFactory.MOCK_OFFICER();
        String pf = mockOfficer.getPf();

        // mock dataTableColumn with DataTableColumnAccessList
        DataTableColumn mockDataTableColumn = DataTableColumnStubFactory.MOCK_DATATABLECOLUMN_NOACCESSLIST();

        List<DataTableColumnAccess> dataTableColumnAccessList = new ArrayList<>();
        dataTableColumnAccessList.add(new DataTableColumnAccess(mockDataTableColumn, "Pf", "999"));
        mockDataTableColumn.setDataTableColumnAccessList(dataTableColumnAccessList);

        //act
        doReturn(Optional.of(mockOfficer)).when(officerRepository).findByPf(anyString());
        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTableColumn));

        // assert
        assertFalse(() -> dataTableColumnService.ValidateOfficerDataTableColumnAccess(pf, anyLong()));

        // clear dataTableColumnAccessList
        mockDataTableColumn.getDataTableColumnAccessList().clear();
    }

    @Test
    public void ValidateOfficerDataTableColumnAccess_GivenOfficerPfAndPfInDataTableColumnAccess_ShouldReturnTrue(){
        // arrange
        // mock officer with Ddcs list
        Officer mockOfficer = DataTableColumnStubFactory.MOCK_OFFICER();
        String pf = mockOfficer.getPf();

        // mock dataTableColumn with DataTableColumnAccessList
        DataTableColumn mockDataTableColumn = DataTableColumnStubFactory.MOCK_DATATABLECOLUMN_NOACCESSLIST();

        List<DataTableColumnAccess> dataTableColumnAccessList = new ArrayList<>();
        dataTableColumnAccessList.add(new DataTableColumnAccess(mockDataTableColumn, "Pf", "123"));
        mockDataTableColumn.setDataTableColumnAccessList(dataTableColumnAccessList);

        //act
        doReturn(Optional.of(mockOfficer)).when(officerRepository).findByPf(anyString());
        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTableColumn));

        // assert
        assertTrue(() -> dataTableColumnService.ValidateOfficerDataTableColumnAccess(pf, anyLong()));

        // clear dataTableColumnAccessList
        mockDataTableColumn.getDataTableColumnAccessList().clear();
    }

    @Test
    public void addOfficerDataTableColumnAccess_DataTableColumnNotPresent_ShouldThrowException() {
        // act
        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.empty());

        // assert
        assertThrows(DataTableColumnNotFoundException.class, () -> dataTableColumnService.addOfficerDataTableColumnAccess("123", "123"));
    }

    @Test
    public void addOfficerDataTableColumnAccess_DataTableColumnPresentAndOfficerPFInDataTableColumnAccessList_ShouldReturnTrue() {
        // arrange
        DataTableColumn mockDataTableColumn = DataTableColumnStubFactory.MOCK_DATATABLECOLUMN_NOACCESSLIST();
        List<DataTableColumnAccess> dataTableColumnAccessList = new ArrayList<>();
        dataTableColumnAccessList.add(new DataTableColumnAccess(mockDataTableColumn, "Pf", "123"));
        mockDataTableColumn.setDataTableColumnAccessList(dataTableColumnAccessList);

        // arrange
        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTableColumn));

        // assert
        assertTrue(() -> dataTableColumnService.addOfficerDataTableColumnAccess("123", "123"));

        // clear dataTableColumnAccessList
        mockDataTableColumn.getDataTableColumnAccessList().clear();
    }

    @Test
    public void addOfficerDataTableColumnAccess_DataTableColumnPresentAndOfficerPFNotInDataTableColumnAccessList_ShouldReturnTrue() {
        // arrange
        DataTableColumn mockDataTableColumn = DataTableColumnStubFactory.MOCK_DATATABLECOLUMN_NOACCESSLIST();

        // arrange
        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTableColumn));

        // assert
        assertTrue(() -> dataTableColumnService.addOfficerDataTableColumnAccess("123", "123"));
    }

    @Test
    public void removeOfficerDataTableColumnAccess_DataTableColumnNotPresent_ShouldThrowException() {
        // act
        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.empty());

        // assert
        assertThrows(DataTableColumnNotFoundException.class, () -> dataTableColumnService.removeOfficerDataTableColumnAccess("123", "123"));
    }

    @Test
    public void removeOfficerDataTableColumnAccess_DataTableColumnPresentAndOfficerPFInDataTableColumnAccessList_ShouldReturnTrue() {
        // arrange
        DataTableColumn mockDataTableColumn = DataTableColumnStubFactory.MOCK_DATATABLECOLUMN_NOACCESSLIST();
        List<DataTableColumnAccess> dataTableColumnAccessList = new ArrayList<>();
        dataTableColumnAccessList.add(new DataTableColumnAccess(mockDataTableColumn, "Pf", "123"));
        mockDataTableColumn.setDataTableColumnAccessList(dataTableColumnAccessList);

        // arrange
        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTableColumn));

        // assert
        assertTrue(() -> dataTableColumnService.removeOfficerDataTableColumnAccess("123", "123"));

        // clear dataTableColumnAccessList
        mockDataTableColumn.getDataTableColumnAccessList().clear();
    }

    @Test
    public void removeOfficerDataTableColumnAccess_DataTableColumnPresentAndOfficerPFNotInDataTableColumnAccessList_ShouldReturnTrue() {
        // arrange
        DataTableColumn mockDataTableColumn = DataTableColumnStubFactory.MOCK_DATATABLECOLUMN_NOACCESSLIST();

        // arrange
        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTableColumn));

        // assert
        assertTrue(() -> dataTableColumnService.removeOfficerDataTableColumnAccess("123", "123"));
    }

    @Test
    public void editDataTableColumnDescription_DataTableColumnDoesNotExist_ShouldThrowException() {
        // assert
        assertThrows(DataTableColumnNotFoundException.class, () -> dataTableColumnService.editDataTableColumnDescription("mock", anyLong(), "123"));
    }

    @Test
    public void editDataTableColumnDescription_OfficerDoesNotExist_ShouldThrowException() {
        // arrange
        DataTableColumn mockDataTableColumn = DataTableColumnStubFactory.MOCK_DATATABLECOLUMN_NOACCESSLIST();

        // act
        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTableColumn));

        // assert
        assertThrows(OfficerNotFoundException.class, () -> dataTableColumnService.editDataTableColumnDescription("mock", anyLong(), "123"));
    }

    @Test
    public void editDataTableColumnDescription_OfficerNotCustodianOrOwner_ShouldThrowException() {
        // arrange
        DataTableColumn mockDataTableColumn = DataTableColumnStubFactory.MOCK_DATATABLECOLUMN_NOACCESSLIST();
        Officer mockOfficer2 = DataTableColumnStubFactory.MOCK_OFFICER2();

        // act
        when(officerRepository.findByPf(anyString())).thenReturn(Optional.of(mockOfficer2));
        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTableColumn));

        // assert
        assertThrows(DatasetAccessNotFoundException.class, () -> dataTableColumnService.editDataTableColumnDescription("mock", Long.parseLong("123"), "456"));
    }

    @Test
    public void editDataTableColumnDescription_DataTableColumnExistAndOfficerIsOwner_ShouldReturnTrue() {
        // arrange
        DataTableColumn mockDataTableColumn = DataTableColumnStubFactory.MOCK_DATATABLECOLUMN_NOACCESSLIST();
        Officer mockOfficer = DataTableColumnStubFactory.MOCK_OFFICER();

        // act
        when(officerRepository.findByPf(anyString())).thenReturn(Optional.of(mockOfficer));
        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTableColumn));

        // assert
        assertTrue(() -> dataTableColumnService.editDataTableColumnDescription("mock", Long.parseLong("123"), "123"));
    }

    @Test
    public void editDataTableColumnDescription_DataTableColumnExistAndOfficerIsCustodian_ShouldReturnTrue() {
        // arrange
        DataTableColumn mockDataTableColumn = DataTableColumnStubFactory.MOCK_DATATABLECOLUMN_NOACCESSLIST();
        Officer mockOfficer2 = DataTableColumnStubFactory.MOCK_OFFICER2();
        mockDataTableColumn.getDataTable().getDataset().getOfficerCustodianList().add(mockOfficer2); // add mockOfficer2 temporarily as custodian

        // act
        when(officerRepository.findByPf(anyString())).thenReturn(Optional.of(mockOfficer2));
        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTableColumn));

        // assert
        assertTrue(() -> dataTableColumnService.editDataTableColumnDescription("mock", Long.parseLong("123"), "456"));

        // empty custodian list
        mockDataTableColumn.getDataTable().getDataset().getOfficerCustodianList().remove(mockOfficer2);
    }

//    test case for set access level for datatable column
    @Test
    public void editDataTableColumnPrivacy_OfficerDoesNotExist_ShouldThrowException() {
        assertThrows(OfficerNotFoundException.class, () -> dataTableColumnService.editDataTableColumnPrivacy(new ArrayList<>(), new ArrayList<>(), "123"));
    }

    @Test
    public void editDataTableColumnPrivacy_OfficerNotCustodianOrOwner_ShouldThrowException() {
        // arrange
        List<Long> dataTableColumnIdList = DataTableColumnStubFactory.MOCK_DATATABLECOLUMNIDLIST();
        List<Boolean> dataTableColumnPrivacyList = DataTableColumnStubFactory.MOCK_DATATABLECOLUMNPRIVACYLIST_MIXED();
        DataTableColumn mockDataTableColumn = DataTableColumnStubFactory.MOCK_DATATABLECOLUMN_NOACCESSLIST();
        Officer mockOfficer2 = DataTableColumnStubFactory.MOCK_OFFICER2();

        // act
        when(officerRepository.findByPf(anyString())).thenReturn(Optional.of(mockOfficer2));
        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTableColumn));

        // assert
        assertThrows(DatasetAccessNotFoundException.class, () -> dataTableColumnService.editDataTableColumnPrivacy(dataTableColumnIdList, dataTableColumnPrivacyList, "123"));
    }

    @Test
    public void editDataTableColumnPrivacy_isPublicDataTableExistAndOfficerIsOwner_ShouldReturnTrue() {
        // arrange
        List<Long> dataTableColumnIdList = DataTableColumnStubFactory.MOCK_DATATABLECOLUMNIDLIST();
        List<Boolean> dataTableColumnPrivacyList = DataTableColumnStubFactory.MOCK_DATATABLECOLUMNPRIVACYLIST_MIXED();
        DataTableColumn mockDataTableColumn = DataTableColumnStubFactory.MOCK_DATATABLECOLUMN_NOACCESSLIST();
        Officer mockOfficer = DataTableColumnStubFactory.MOCK_OFFICER();

        // act
        when(officerRepository.findByPf(anyString())).thenReturn(Optional.of(mockOfficer));
        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTableColumn));

        // assert
        assertTrue(() -> dataTableColumnService.editDataTableColumnPrivacy(dataTableColumnIdList, dataTableColumnPrivacyList, "123"));
    }

    @Test
    public void editDataTableColumnPrivacy_isPublicDataTableExistAndOfficerIsCustodian_ShouldReturnTrue() {
        // arrange
        List<Long> dataTableColumnIdList = DataTableColumnStubFactory.MOCK_DATATABLECOLUMNIDLIST();
        List<Boolean> dataTableColumnPrivacyList = DataTableColumnStubFactory.MOCK_DATATABLECOLUMNPRIVACYLIST_MIXED();
        DataTableColumn mockDataTableColumn = DataTableColumnStubFactory.MOCK_DATATABLECOLUMN_NOACCESSLIST();
        Officer mockOfficer = DataTableColumnStubFactory.MOCK_OFFICER();
        Officer mockOfficer2 = DataTableColumnStubFactory.MOCK_OFFICER2();
        mockDataTableColumn.getDataTable().getDataset().getOfficerCustodianList().add(mockOfficer2); // add mockOfficer2 temporarily as custodian

        // act
        when(officerRepository.findByPf(anyString())).thenReturn(Optional.of(mockOfficer));
        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTableColumn));

        // assert
        assertTrue(() -> dataTableColumnService.editDataTableColumnPrivacy(dataTableColumnIdList, dataTableColumnPrivacyList, "123"));

        // empty custodian list
        mockDataTableColumn.getDataTable().getDataset().getOfficerCustodianList().remove(mockOfficer2);
    }

    @Test
    public void editDataTableColumnPrivacy_isPrivateDataTableExistAndOfficerIsOwner_ShouldReturnTrue() {
        // arrange
        List<Long> dataTableColumnIdList = DataTableColumnStubFactory.MOCK_DATATABLECOLUMNIDLIST();
        List<Boolean> dataTableColumnPrivacyList = DataTableColumnStubFactory.MOCK_DATATABLECOLUMNPRIVACYLIST_MIXED();
        DataTableColumn mockDataTableColumn = DataTableColumnStubFactory.MOCK_DATATABLECOLUMN_NOACCESSLIST();
        Officer mockOfficer = DataTableColumnStubFactory.MOCK_OFFICER();

        // act
        when(officerRepository.findByPf(anyString())).thenReturn(Optional.of(mockOfficer));
        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTableColumn));

        // assert
        assertTrue(() -> dataTableColumnService.editDataTableColumnPrivacy(dataTableColumnIdList, dataTableColumnPrivacyList, "123"));
    }


//@Test
//public void editDataTableColumnPrivacy_DataTableColumnDoesNotExist_ShouldThrowException() {
//    // assert
//    List<String> dataTableColumnList = new ArrayList<>();
//    dataTableColumnList.add(0,"1");
//    dataTableColumnList.add(1,"true");
//    assertThrows(DataTableColumnNotFoundException.class, () -> dataTableColumnService.editDataTableColumnPrivacy(dataTableColumnList,"123"));
//}
//
//    @Test
//    public void editDataTableColumnPrivacy_OfficerDoesNotExist_ShouldThrowException() {
//        // arrange
//        DataTableColumn mockDataTableColumn = DataTableColumnStubFactory.MOCK_DATATABLECOLUMN_NOACCESSLIST();
//        List<String> dataTableColumnList = new ArrayList<>();
//        dataTableColumnList.add(0,"1");
//        dataTableColumnList.add(1,"true");
//        // act
//        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTableColumn));
//
//        // assert
//        assertThrows(OfficerNotFoundException.class, () -> dataTableColumnService.editDataTableColumnPrivacy(dataTableColumnList, "123"));
//    }
//
//    @Test
//    public void editDataTableColumnPrivacy_OfficerNotCustodianOrOwner_ShouldThrowException() {
//        // arrange
//        DataTableColumn mockDataTableColumn = DataTableColumnStubFactory.MOCK_DATATABLECOLUMN_NOACCESSLIST();
//        Officer mockOfficer2 = DataTableColumnStubFactory.MOCK_OFFICER2();
//        List<String> dataTableColumnList = new ArrayList<>();
//        dataTableColumnList.add(0,"1");
//        dataTableColumnList.add(1,"true");
//
//        // act
//        when(officerRepository.findByPf(anyString())).thenReturn(Optional.of(mockOfficer2));
//        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTableColumn));
//
//        // assert
//        assertThrows(DatasetAccessNotFoundException.class, () -> dataTableColumnService.editDataTableColumnPrivacy(dataTableColumnList ,"123"));
//    }

//    @Test
//    public void editDataTableColumnPrivacy_DataTableColumnExistAndOfficerIsOwner_ShouldReturnTrue() {
//        // arrange
//        DataTableColumn mockDataTableColumn = DataTableColumnStubFactory.MOCK_DATATABLECOLUMN_NOACCESSLIST();
//        Officer mockOfficer = DataTableColumnStubFactory.MOCK_OFFICER();
//        List<String> dataTableColumnList = new ArrayList<>();
//        dataTableColumnList.add(0,"1");
//        dataTableColumnList.add(1,"true");
//        // act
//        when(officerRepository.findByPf(anyString())).thenReturn(Optional.of(mockOfficer));
//        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTableColumn));
//
//        // assert
//        assertTrue(() -> dataTableColumnService.editDataTableColumnPrivacy(dataTableColumnList , "123"));
//    }
//
//    @Test
//    public void editDataTableColumnPrivacy_DataTableColumnExistAndOfficerIsCustodian_ShouldReturnTrue() {
//        // arrange
//        DataTableColumn mockDataTableColumn = DataTableColumnStubFactory.MOCK_DATATABLECOLUMN_NOACCESSLIST();
//        Officer mockOfficer2 = DataTableColumnStubFactory.MOCK_OFFICER2();
//        mockDataTableColumn.getDataTable().getDataset().getOfficerCustodianList().add(mockOfficer2); // add mockOfficer2 temporarily as custodian
//        List<String> dataTableColumnList = new ArrayList<>();
//        dataTableColumnList.add(0,"1");
//        dataTableColumnList.add(1,"true");
//        // act
//        when(officerRepository.findByPf(anyString())).thenReturn(Optional.of(mockOfficer2));
//        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTableColumn));
//
//        // assert
//        assertTrue(() -> dataTableColumnService.editDataTableColumnPrivacy(dataTableColumnList, "123"));
//
//        // empty custodian list
//        mockDataTableColumn.getDataTable().getDataset().getOfficerCustodianList().remove(mockOfficer2);
//    }

}
