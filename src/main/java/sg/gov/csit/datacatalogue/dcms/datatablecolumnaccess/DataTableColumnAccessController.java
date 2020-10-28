package sg.gov.csit.datacatalogue.dcms.datatablecolumnaccess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sg.gov.csit.datacatalogue.dcms.datatablecolumn.DataTableColumnService;

@RestController
@RequestMapping("/datatablecolumnaccess")
public class DataTableColumnAccessController {
    @Autowired
    DataTableColumnAccessService dataTableColumnAccessService;

    @PostMapping("/add-officer-datatablecolumnaccsess")
    public boolean addOfficerDataTableColumnAccess(@RequestAttribute("txnId") String txnId,
                                                   @RequestAttribute("pf") String pf,
                                                   @RequestParam("officerPf") String officerPf,
                                                   @RequestParam("dataTableColumnId") String dataTableColumnId) {
        return dataTableColumnAccessService.addOfficerDataTableColumnAccess(officerPf, dataTableColumnId);
    }

    @DeleteMapping("/remove-officer-datatablecolumnaccess")
    public boolean removeOfficerDataTableColumnAccess(@RequestAttribute("txnId") String txnId,
                                                @RequestAttribute("pf") String pf,
                                                @RequestParam("officerPf") String officerPf,
                                                @RequestParam("dataTableColumnId") String dataTableColumnId) {
        return dataTableColumnAccessService.removeOfficerDataTableColumnAccess(officerPf, dataTableColumnId);
    }
}
