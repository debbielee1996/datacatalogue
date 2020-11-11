package sg.gov.csit.datacatalogue.dcms.datasetaccess;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;
import sg.gov.csit.datacatalogue.dcms.dataset.DatasetRepository;
import sg.gov.csit.datacatalogue.dcms.dataset.DatasetService;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTableService;
import sg.gov.csit.datacatalogue.dcms.datatablecolumn.DataTableColumn;
import sg.gov.csit.datacatalogue.dcms.datatablecolumn.DataTableColumnService;

import java.util.List;

@AllArgsConstructor
@Service
public class DatasetAccessService {
    @Autowired
    private DatasetAccessRepository datasetAccessRepository;

    @Autowired
    DatasetService datasetService;

    @Autowired
    DatasetRepository datasetRepository;

    @Autowired
    DataTableService dataTableService;

    @Autowired
    DataTableColumnService dataTableColumnService;

    public boolean addOfficerDatasetAccess(String officerPf, String datasetId) {
        datasetService.addOfficerDatasetAccess(officerPf, datasetId);
        Dataset dataset = datasetRepository.findById(Long.parseLong(datasetId)).get();

        List<DataTable> dataTableList = dataset.getDataTableList();
        // add access rights for each DataTable
        for (DataTable dt:dataTableList) {
            dataTableService.addOfficerDataTableAccess(officerPf, Long.toString(dt.getId()));
            List<DataTableColumn> dataTableColumnList = dt.getDataTableColumnList();

            // add access rights for each DataTableColumn
            for (DataTableColumn dtc:dataTableColumnList) {
                dataTableColumnService.addOfficerDataTableColumnAccess(officerPf, Long.toString(dtc.getId()));
            }
        }
        return true;
    }

    public boolean removeOfficerDatasetAccess(String officerPf, String datasetId) {
        datasetService.removeOfficerDatasetAccess(officerPf, datasetId);
        Dataset dataset = datasetRepository.findById(Long.parseLong(datasetId)).get();

        List<DataTable> dataTableList = dataset.getDataTableList();
        // remove access rights for each DataTable
        for (DataTable dt:dataTableList) {
            dataTableService.removeOfficerDataTableAccess(officerPf, Long.toString(dt.getId()));
            List<DataTableColumn> dataTableColumnList = dt.getDataTableColumnList();

            // remove access rights for each DataTableColumn
            for (DataTableColumn dtc:dataTableColumnList) {
                dataTableColumnService.removeOfficerDataTableColumnAccess(officerPf, Long.toString(dtc.getId()));
            }
        }
        return true;
    }
}
