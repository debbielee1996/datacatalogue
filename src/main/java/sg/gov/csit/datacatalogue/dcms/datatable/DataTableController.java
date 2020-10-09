package sg.gov.csit.datacatalogue.dcms.datatable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/datatable")
public class DataTableController {
    @Autowired
    DataTableService dataTableService;

    @GetMapping("/get-all-datatables")
    public List<DataTable> getAllDatatables() {
        return dataTableService.getAllDatatables();
    }

    @PostMapping("/upload-file")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("tableName") String tableName,
                             @RequestParam("datasetId") String datasetId,
                             @RequestParam("description") String description) throws Exception {
        return dataTableService.uploadFile(file, tableName, datasetId, description);
    }
}