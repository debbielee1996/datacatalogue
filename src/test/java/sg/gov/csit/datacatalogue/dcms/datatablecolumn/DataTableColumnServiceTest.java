package sg.gov.csit.datacatalogue.dcms.datatablecolumn;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;
import sg.gov.csit.datacatalogue.dcms.datatableaccess.DataTableAccess;
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
        long dataTableColumnId = 321;

        // act and assert
        assertThrows(OfficerNotFoundException.class,
                () -> dataTableColumnService.ValidateOfficerDataTableColumnAccess(pf,dataTableColumnId));
    }

    @Test
    public void ValidateOfficerDataTableColumnAccess_GivenOfficerPfAndDataTableColumnNotInDb_ShouldThrowException(){
        //arrange
        String pf = "123";
        long dataTableColumnId = 321;

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
        String pf = "123";
        Officer mockOfficer = new Officer(pf,"test","testEmail", "123", "System Admin");
        long dataTableId = 321;
        Dataset mockDataset = new Dataset("mock", "mock", mockOfficer);
        DataTable mockDataTable = new DataTable("mock", "mock", mockDataset, mockOfficer);
        DataTableColumn mockDataTableColumn = new DataTableColumn("mock", "mock", "Text", mockDataTable);
        mockDataTable.getDataTableColumnList().add(mockDataTableColumn);

        // act
        doReturn(true).when(officerService).IsOfficerInDatabase(anyString());
        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTableColumn));

        // assert
        assertFalse(() -> dataTableColumnService.ValidateOfficerDataTableColumnAccess(pf,dataTableId));
    }

    @Test
    public void ValidateOfficerDataTableColumnAccess_GivenOfficerPfNotInDataTableColumnAccess_ShouldReturnFalse(){
        // arrange
        // mock officer with Ddcs list
        String pf = "123";
        Officer mockOfficer = new Officer(pf,"test","testEmail", "123", "System Admin");

        // mock dataTableColumn with DataTableColumnAccessList
        Dataset mockDataset = new Dataset("mock", "mock", mockOfficer);
        DataTable mockDataTable = new DataTable("mock", "mock", mockDataset, mockOfficer);
        DataTableColumn mockDataTableColumn = new DataTableColumn("mock", "mock", "Text", mockDataTable);
        mockDataTable.getDataTableColumnList().add(mockDataTableColumn);

        List<DataTableColumnAccess> dataTableColumnAccessList = new ArrayList<>();
        dataTableColumnAccessList.add(new DataTableColumnAccess(mockDataTableColumn, "Pf", "999"));
        mockDataTableColumn.setDataTableColumnAccessList(dataTableColumnAccessList);

        //act
        doReturn(true).when(officerService).IsOfficerInDatabase(anyString());
        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTableColumn));

        // assert
        assertFalse(() -> dataTableColumnService.ValidateOfficerDataTableColumnAccess(pf, anyLong()));
    }

    @Test
    public void ValidateOfficerDataTableColumnAccess_GivenOfficerPfAndPfInDataTableColumnAccess_ShouldReturnTrue(){
        // arrange
        // mock officer with Ddcs list
        String pf = "123";
        Officer mockOfficer = new Officer(pf,"test","testEmail", "123", "System Admin");

        // mock dataTableColumn with DataTableColumnAccessList
        Dataset mockDataset = new Dataset("mock", "mock", mockOfficer);
        DataTable mockDataTable = new DataTable("mock", "mock", mockDataset, mockOfficer);
        DataTableColumn mockDataTableColumn = new DataTableColumn("mock", "mock", "Text", mockDataTable);
        mockDataTable.getDataTableColumnList().add(mockDataTableColumn);

        List<DataTableColumnAccess> dataTableColumnAccessList = new ArrayList<>();
        dataTableColumnAccessList.add(new DataTableColumnAccess(mockDataTableColumn, "Pf", "123"));
        mockDataTableColumn.setDataTableColumnAccessList(dataTableColumnAccessList);

        //act
        doReturn(true).when(officerService).IsOfficerInDatabase(anyString());
        when(dataTableColumnRepository.findById(anyLong())).thenReturn(Optional.of(mockDataTableColumn));

        // assert
        assertTrue(() -> dataTableColumnService.ValidateOfficerDataTableColumnAccess(pf, anyLong()));
    }

}
