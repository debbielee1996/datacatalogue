package sg.gov.csit.datacatalogue.dcms.dataset;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTableDto;

import java.util.List;

@RestController
@RequestMapping("/dataset")
public class DatasetController {
    @Autowired
    DatasetService datasetService;

    @PostMapping("/create-new-dataset")
    public boolean createNewDataset(@RequestParam("name") String name,
                                   @RequestParam("description") String description) {
                                   //@RequestParam("pf") String pf) {
        // hard code pf for now
        String pf = "1001";
        return datasetService.createNewDataset(name, description, pf);
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

    @DeleteMapping("/delete-dataset/{datasetId}")
    public boolean deleteDataset(@PathVariable("datasetId") String datasetId) {
        return datasetService.deleteDataset(datasetId);
    }
}
