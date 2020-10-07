package sg.gov.csit.datacatalogue.dcms.exception;

public class OfficerNotFoundException extends RuntimeException{
    public OfficerNotFoundException(String pf) {
        super("Officer with pf: " + pf + " not found in database");
    }
}
