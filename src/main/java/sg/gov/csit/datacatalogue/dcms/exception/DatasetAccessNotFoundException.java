package sg.gov.csit.datacatalogue.dcms.exception;

public class DatasetAccessNotFoundException extends RuntimeException {
    public DatasetAccessNotFoundException(String pf, long datasetId) {
        super("DatasetAccess not found for officer "+ pf +" for dataset "+ datasetId);
    }
}
