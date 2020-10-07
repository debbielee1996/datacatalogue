package sg.gov.csit.datacatalogue.dcms;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;
import sg.gov.csit.datacatalogue.dcms.officer.OfficerRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object Handler) throws Exception{
        String txnId = UUID.randomUUID().toString();

        request.setAttribute("UUID",txnId);
        request.setAttribute("Pf","123");
        return true;
    }
}
