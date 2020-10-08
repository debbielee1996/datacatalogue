package sg.gov.csit.datacatalogue.dcms.exception;

public class DatasetExistsException extends RuntimeException {
    public DatasetExistsException(String datasetName) {
        super("Dataset " + datasetName + " already exists in database");
    }
}
