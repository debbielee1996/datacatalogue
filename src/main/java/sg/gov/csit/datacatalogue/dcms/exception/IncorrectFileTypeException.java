package sg.gov.csit.datacatalogue.dcms.exception;

public class IncorrectFileTypeException extends RuntimeException {
    public IncorrectFileTypeException(String ext) { super("File extension "+ ext +" not supported");
    }
}
