package sg.gov.csit.datacatalogue.dcms.exception;

public class DataTableColumnNotFoundException extends RuntimeException {
    public DataTableColumnNotFoundException(Long dataTableColumnId) {
        super("DataTableColumn with id: " + dataTableColumnId + " not found in database");
    }
}
