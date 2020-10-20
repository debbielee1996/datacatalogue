package sg.gov.csit.datacatalogue.dcms.interceptors;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import sg.gov.csit.datacatalogue.dcms.exception.OfficerNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String txnId = UUID.randomUUID().toString();
        request.setAttribute("txnId", txnId);
        request.setAttribute("pf", "1001");

        return true;
    }
}
