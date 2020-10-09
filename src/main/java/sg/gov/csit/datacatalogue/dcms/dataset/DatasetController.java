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
    public boolean createNewDataset(@RequestParam("name") String name,
                                   @RequestParam("description") String description) {
        return datasetService.createNewDataset(name, description);
    }

    @GetMapping("/get-all-datasets")
    public List<Dataset> getAllDatasets() {
        return datasetService.getAllDatasets();
    }
}
