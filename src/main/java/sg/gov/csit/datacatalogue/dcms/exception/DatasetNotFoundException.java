package sg.gov.csit.datacatalogue.dcms.exception;

public class DatasetNotFoundException extends RuntimeException{
    public DatasetNotFoundException(long id){
        super("Dataset with id: " + id + " not found in database");
    }
}
