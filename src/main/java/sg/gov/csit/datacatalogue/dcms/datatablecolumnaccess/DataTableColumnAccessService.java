package sg.gov.csit.datacatalogue.dcms.datatablecolumnaccess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.gov.csit.datacatalogue.dcms.dataset.DatasetService;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTableService;
import sg.gov.csit.datacatalogue.dcms.datatablecolumn.DataTableColumn;
import sg.gov.csit.datacatalogue.dcms.datatablecolumn.DataTableColumnRepository;
import sg.gov.csit.datacatalogue.dcms.datatablecolumn.DataTableColumnService;

import java.util.List;

@Service
public class DataTableColumnAccessService {
    @Autowired
    DatasetService datasetService;

    @Autowired
    DataTableService dataTableService;

    @Autowired
    DataTableColumnService dataTableColumnService;

    @Autowired
    DataTableColumnRepository dataTableColumnRepository;

    public boolean addOfficerDataTableColumnAccess(String officerPf, String dataTableColumnId) {
        dataTableColumnService.addOfficerDataTableColumnAccess(officerPf, dataTableColumnId);
        DataTableColumn dataTableColumn = dataTableColumnRepository.findById(Long.parseLong(dataTableColumnId)).get();

        // add access rights for parent DataTable
        dataTableService.addOfficerDataTableAccess(officerPf, Long.toString(dataTableColumn.getDataTable().getId()));

        // add access rights for parent Dataset
        datasetService.addOfficerDatasetAccess(officerPf, Long.toString(dataTableColumn.getDataTable().getDataset().getId()));

        return true;
    }

    public boolean removeOfficerDataTableColumnAccess(String officerPf, String dataTableColumnId) {
        return dataTableColumnService.removeOfficerDataTableColumnAccess(officerPf, dataTableColumnId);
    }
}
