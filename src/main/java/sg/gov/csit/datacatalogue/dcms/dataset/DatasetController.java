package sg.gov.csit.datacatalogue.dcms.dataset;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/dataset")
public class DatasetController {
    @Autowired
    DatasetService datasetService;

    @PostMapping("/create-new-dataset")
    public boolean createNewDataset(@RequestAttribute("txnId") String txnId,
                                    @RequestAttribute("pf") String pf,
                                    @RequestParam("name") String name,
                                   @RequestParam("description") String description,
                                    @RequestParam("custodianPfs") List<String> custodianPfs,
                                    @RequestParam("ownerPf") String ownerPf,
                                    @RequestParam("isPublic") Boolean isPublic) {
        return datasetService.createNewDataset(name, description, pf, custodianPfs, ownerPf, isPublic);
    }

    @GetMapping("/dataset/{id}")
    public boolean ValidateOfficerDatasetAccess(@RequestAttribute("txnId") String txnId,
                                                @RequestAttribute("pf") String pf,
                                                @PathVariable("id") long datasetId){
        return datasetService.ValidateOfficerDatasetAccess(pf,datasetId);
    }

    @GetMapping("/get-all-datasets-created")
    public List<DatasetDto> getDatasetsCreatedByOfficer(@RequestAttribute("txnId") String txnId,
                                                        @RequestAttribute("pf") String pf) {
        return datasetService.getDatasetsCreatedByOfficer(pf);
    }

    @GetMapping("/get-all-dataset-dtos")
    public List<DatasetDto> getAllDatasetDtos(@RequestAttribute("txnId") String txnId,
                                              @RequestAttribute("pf") String pf) {
        return datasetService.getAllDatasetDtos(pf);
    }

    @GetMapping("/get-all-public-dataset-dtos")
    public List<DatasetDto> getAllPublicDatasetDtos(@RequestAttribute("txnId") String txnId,
                                              @RequestAttribute("pf") String pf) {
        return datasetService.getAllPublicDatasetDtos(pf);
    }

    @PostMapping("/add-officer-to-custodian-list")
    public boolean addOfficerToCustodianList(@RequestAttribute("txnId") String txnId,
                                             @RequestAttribute("pf") String pf,
                                             @RequestParam("custodianPf") String custodianPf,
                                             @RequestParam("datasetId") long datasetId) {
        return datasetService.addOfficerToCustodianList(custodianPf, datasetId);
    }

    @GetMapping("/datasetname-isunique")
    public boolean datasetNameIsUnique(@RequestAttribute("txnId") String txnId,
                                     @RequestAttribute("pf") String pf,
                                     @RequestParam("datasetName") String datasetName) {
        return datasetService.datasetNameIsUnique(datasetName);
    }
//    edit access level of the dataset
    @PostMapping("/edit-privacy")
    public boolean editDataSetPrivacy(@RequestAttribute("txnId") String txnId,
                                      @RequestAttribute("pf") String pf,
                                      @RequestParam("datasetId") long datasetId,
                                      @RequestParam("isPublic") Boolean isPublic) {
        return datasetService.editDataSetPrivacy(isPublic,datasetId, pf);
    }
}
