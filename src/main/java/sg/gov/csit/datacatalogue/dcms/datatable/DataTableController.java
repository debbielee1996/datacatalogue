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

    @PostMapping("/upload-file")
    public boolean uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("tableName") String tableName,
                             @RequestParam("datasetId") String datasetId,
                             @RequestParam("description") String description,
                             @RequestParam("dataTypes") List<String> dataTypes
//                              @RequestParam("pf") String pf
    ) throws IOException, CsvException, SQLException {
        String pf = "1001"; // hard code for now
        return dataTableService.uploadFile(file, tableName, datasetId, description, dataTypes, pf);
    }

    @GetMapping("/get-all-datatable-dtos")
    public List<DataTableDto> getAllDataTableDtos() {
        return dataTableService.getAllDataTableDtos();
    }
}
