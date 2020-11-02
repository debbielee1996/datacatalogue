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
                             @RequestParam("dataTypes") List<String> dataTypes,
                             @RequestParam("dataColDescriptions") List<String> dataColDescriptions
//                              @RequestParam("pf") String pf
    ) throws IOException, CsvException, SQLException {
        String pf = "1001"; // hard code for now
        return dataTableService.uploadFile(file, tableName, datasetId, description, dataTypes, pf, dataColDescriptions);
    }

    @GetMapping("/get-all-datatable-dtos")
    public List<DataTableDto> getAllDataTableDtos(@RequestAttribute("pf") String pf) {
        return dataTableService.getAllDataTableDtos(pf);
    }

    @GetMapping("/get-dataset-datatables/{datasetId}")
    public List<DataTableDto> getDataTablesOfDataset(@RequestAttribute("pf") String pf,
                                                    @PathVariable("datasetId") String datasetId) {
        return dataTableService.getDataTablesOfDataset(pf, datasetId);
    }

    @GetMapping("/datatable/{dataTableId}")
    public boolean ValidateOfficerDataTableAccess(@RequestAttribute("txnId") String txnId,
                                                @RequestAttribute("pf") String pf,
                                                @PathVariable("dataTableId") long dataTableId){
        return dataTableService.ValidateOfficerDataTableAccess(pf,dataTableId);
    }

    @GetMapping("/get-all-datatables-created")
    public List<DataTableDto> getDataTablesCreatedByOfficer(@RequestAttribute("txnId") String txnId,
                                                            @RequestAttribute("pf") String pf) {
        return dataTableService.getDataTablesCreatedByOfficer(pf);
    }

    @PostMapping("/edit-description")
    public boolean editDataTableDescription(@RequestAttribute("txnId") String txnId,
                                            @RequestAttribute("pf") String pf,
                                            @RequestParam("dataTableId") long dataTableId,
                                            @RequestParam("description") String description) {
        return dataTableService.editDataTableDescription(description, dataTableId);
    }
}
