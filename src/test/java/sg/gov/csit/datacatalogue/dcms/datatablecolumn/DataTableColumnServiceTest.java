package sg.gov.csit.datacatalogue.dcms.datatablecolumn;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;
import sg.gov.csit.datacatalogue.dcms.datatable.mock.DataTableStubFactory;
import sg.gov.csit.datacatalogue.dcms.datatableaccess.DataTableAccess;
import sg.gov.csit.datacatalogue.dcms.datatablecolumn.mock.DataTableColumnStubFactory;
import sg.gov.csit.datacatalogue.dcms.datatablecolumnaccess.DataTableColumnAccess;
import sg.gov.csit.datacatalogue.dcms.exception.DataTableColumnNotFoundException;
import sg.gov.csit.datacatalogue.dcms.exception.DataTableNotFoundException;
import sg.gov.csit.datacatalogue.dcms.exception.OfficerNotFoundException;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;
import sg.gov.csit.datacatalogue.dcms.officer.OfficerService;

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
    OfficerService officerService;

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

        // act
        doReturn(true).when(officerService).IsOfficerInDatabase(anyString());
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
        doReturn(true).when(officerService).IsOfficerInDatabase(anyString());
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
        doReturn(true).when(officerService).IsOfficerInDatabase(anyString());
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
        doReturn(true).when(officerService).IsOfficerInDatabase(anyString());
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
}
