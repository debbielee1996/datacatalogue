package sg.gov.csit.datacatalogue.dcms.datatableaccess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;
import sg.gov.csit.datacatalogue.dcms.dataset.DatasetService;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTableService;
import sg.gov.csit.datacatalogue.dcms.datatablecolumn.DataTableColumn;
import sg.gov.csit.datacatalogue.dcms.datatablecolumn.DataTableColumnService;

import java.util.List;

@Service
public class DataTableAccessService {
    @Autowired
    DatasetService datasetService;

    @Autowired
    DataTableService dataTableService;

    @Autowired
    DataTableColumnService dataTableColumnService;

    public boolean addOfficerDataTableAccess(String officerPf, String dataTableId) {
        dataTableService.addOfficerDataTableAccess(officerPf, dataTableId);
        DataTable dataTable = dataTableService.getDataTableById(Long.parseLong(dataTableId)).get();

        // add access rights for parent Dataset
        datasetService.addOfficerDatasetAccess(officerPf, Long.toString(dataTable.getDataset().getId()));

        // add access rights for each DataTableColumn
        List<DataTableColumn> dataTableColumnList = dataTable.getDataTableColumnList();
        for (DataTableColumn dtc:dataTableColumnList) {
            dataTableColumnService.addOfficerDataTableColumnAccess(officerPf, Long.toString(dtc.getId()));
        }
        return true;
    }

    public boolean removeOfficerDataTableAccess(String officerPf, String dataTableId) {
        dataTableService.removeOfficerDataTableAccess(officerPf, dataTableId);
        DataTable dataTable = dataTableService.getDataTableById(Long.parseLong(dataTableId)).get();

        // remove access rights for each DataTableColumn
        List<DataTableColumn> dataTableColumnList = dataTable.getDataTableColumnList();
        for (DataTableColumn dtc:dataTableColumnList) {
            dataTableColumnService.removeOfficerDataTableColumnAccess(officerPf, Long.toString(dtc.getId()));
        }
        return true;
    }
}
