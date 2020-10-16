package sg.gov.csit.datacatalogue.dcms.datatable;

import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
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
    public boolean uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("tableName") String tableName,
                             @RequestParam("datasetId") String datasetId,
                             @RequestParam("description") String description,
                             @RequestParam("dataTypes") List<String> dataTypes) throws IOException, CsvException, SQLException {
        return dataTableService.uploadFile(file, tableName, datasetId, description, dataTypes);
    }

    @GetMapping("/get-all-datatable-names")
    public List<String> getAllDatatableNames() {
        return dataTableService.getAllDataTableNames();
    }
}
