package sg.gov.csit.datacatalogue.dcms.exception;

public class DataTableNotFoundException extends RuntimeException {
    public DataTableNotFoundException(Long id) {
        super("DataTable with id "+id+" not found in database");
    }
}
