package sg.gov.csit.datacatalogue.dcms.officer;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping("/officer")
@RestController
public class OfficerController {
    @Autowired
    private OfficerService officerService;

    @GetMapping("/dataset/{id}")
    public boolean ValidateOfficerDatasetAccess(//@RequestAttribute("UUID") String txnId,
                                                //@RequestAttribute("Pf") String pf,
                                                @PathVariable("id") long datasetId){
        return officerService.ValidateOfficerDatasetAccess("1001",datasetId);
    }

}
