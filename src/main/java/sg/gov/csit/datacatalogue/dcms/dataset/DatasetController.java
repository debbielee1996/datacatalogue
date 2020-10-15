package sg.gov.csit.datacatalogue.dcms.dataset;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;

import java.util.List;

@RestController
@RequestMapping("/dataset")
public class DatasetController {
    @Autowired
    DatasetService datasetService;

    @PostMapping("/create-new-dataset")
    public boolean createNewDataset(@RequestParam("name") String name,
                                   @RequestParam("description") String description) {
        return datasetService.createNewDataset(name, description);
    }

    @GetMapping("/get-all-datasets")
    public List<Dataset> getAllDatasets() {
        return datasetService.getAllDatasets();
    }

    @GetMapping("/get-dataset-datatables/{datasetId}")
    public List<DataTable> getDataTablesOfDataset(@PathVariable("datasetId") String datasetId) {
        return datasetService.getDataTablesOfDataset(datasetId);
    }

    // this method should not have any authentication because its to verify uniqueness of dataset names
    @GetMapping("/get-all-dataset-names")
    public List<String> getAllDatasetNames() { return datasetService.getAllDatasetNames(); }
}
