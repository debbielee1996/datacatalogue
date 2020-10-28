package sg.gov.csit.datacatalogue.dcms.datatableaccess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/datatableaccess")
public class DataTableAccessController {
    @Autowired
    DataTableAccessService dataTableAccessService;

    @PostMapping("/add-officer-datatableaccsess")
    public boolean addOfficerDataTableAccess(@RequestAttribute("txnId") String txnId,
                                           @RequestAttribute("pf") String pf,
                                           @RequestParam("officerPf") String officerPf,
                                           @RequestParam("dataTableId") String dataTableId) {
        return dataTableAccessService.addOfficerDataTableAccess(officerPf, dataTableId);
    }

    @DeleteMapping("/remove-officer-datatableaccess")
    public boolean removeOfficerDataTableAccess(@RequestAttribute("txnId") String txnId,
                                              @RequestAttribute("pf") String pf,
                                              @RequestParam("officerPf") String officerPf,
                                              @RequestParam("dataTableId") String dataTableId) {
        return dataTableAccessService.removeOfficerDataTableAccess(officerPf, dataTableId);
    }
}
