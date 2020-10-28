package sg.gov.csit.datacatalogue.dcms.datasetaccess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/datasetaccess")
public class DatasetAccessController {
    @Autowired
    DatasetAccessService datasetAccessService;

    @PostMapping("/add-officer-datasetaccsess")
    public boolean addOfficerDatasetAccess(@RequestAttribute("txnId") String txnId,
                                           @RequestAttribute("pf") String pf,
                                           @RequestParam("officerPf") String officerPf,
                                           @RequestParam("datasetId") String datasetId) {
        return datasetAccessService.addOfficerDatasetAccess(officerPf, datasetId);
    }


    @DeleteMapping("/remove-officer-datasetaccess")
    public boolean removeOfficerDatasetAccess(@RequestAttribute("txnId") String txnId,
                                              @RequestAttribute("pf") String pf,
                                              @RequestParam("officerPf") String officerPf,
                                              @RequestParam("datasetId") String datasetId) {
        return datasetAccessService.removeOfficerDatasetAccess(officerPf, datasetId);
    }
}
