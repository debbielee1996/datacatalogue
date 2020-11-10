package sg.gov.csit.datacatalogue.dcms.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception e) {
        e.printStackTrace(); // print stacktrace in back end
        return e.getMessage(); // pass exception msg in data payload
    }
}
