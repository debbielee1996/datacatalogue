package sg.gov.csit.datacatalogue.dcms.datatablecolumn.mock;

import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;
import sg.gov.csit.datacatalogue.dcms.datatablecolumn.DataTableColumn;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;

import java.util.ArrayList;
import java.util.List;

public class DataTableColumnStubFactory {
    public static Officer MOCK_OFFICER() {
        return new Officer("123","test","testEmail", "123", "System Admin");
    }

    public static Officer MOCK_OFFICER2() {
        return new Officer("456","test2","testEmail2", "456", "System Admin");
    }

    public static Dataset MOCK_DATASET_NOACCESSLIST() {
        Dataset dataset = new Dataset("mock", "mock", MOCK_OFFICER(), false);
        dataset.setId(Long.parseLong("123"));
        return dataset;
    }

    public static DataTable MOCK_DATATABLE_NOACCESSLIST() {
        DataTable dataTable = new DataTable("mock", "mock", MOCK_DATASET_NOACCESSLIST(), MOCK_OFFICER(), false);
        dataTable.setId(Long.parseLong("123"));
        return dataTable;
    }

    public static DataTableColumn MOCK_DATATABLECOLUMN_NOACCESSLIST() {
        DataTableColumn dtc = new DataTableColumn("mock", "mock", "Text", MOCK_DATATABLE_NOACCESSLIST(), false);
        dtc.setId(Long.parseLong("1"));
        MOCK_DATATABLE_NOACCESSLIST().getDataTableColumnList().add(dtc);
        return dtc;
    }

    public static List<Boolean> MOCK_DATATABLECOLUMNPRIVACYLIST_MIXED() {
        List<Boolean> dataTableColumnPrivacyList = new ArrayList<>();
        dataTableColumnPrivacyList.add(true);
        return dataTableColumnPrivacyList;
    }

    public static List<Long> MOCK_DATATABLECOLUMNIDLIST() {
        List<Long> dataTableColumnIdList = new ArrayList<>();
        dataTableColumnIdList.add(MOCK_DATATABLECOLUMN_NOACCESSLIST().getId());
        return dataTableColumnIdList;
    }
}
