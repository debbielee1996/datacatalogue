package sg.gov.csit.datacatalogue.dcms.datatablecolumn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/datatablecolumn")
public class DataTableColumnController {
    @Autowired
    DataTableColumnService dataTableColumnService;

    @GetMapping("/get-all-columns-dtos/{dataTableId}")
    public List<DataTableColumnDto> getAllColumnDtos(@PathVariable("dataTableId") String dataTableId,
                                                     @RequestAttribute("pf") String pf) {
        return dataTableColumnService.getAllColumnDtos(pf, dataTableId);
    }

    @GetMapping("/datatablecolumn/{id}")
    public boolean ValidateOfficerDatasetAccess(@RequestAttribute("txnId") String txnId,
                                                @RequestAttribute("pf") String pf,
                                                @PathVariable("id") long dataTableColumnId){
        return dataTableColumnService.ValidateOfficerDataTableColumnAccess(pf,dataTableColumnId);
    }

    @PostMapping("/edit-description")
    public boolean editDataTableColumnDescription(@RequestAttribute("txnId") String txnId,
                                            @RequestAttribute("pf") String pf,
                                            @RequestParam("dataTableColumnId") long dataTableColumnId,
                                            @RequestParam("description") String description) {
        return dataTableColumnService.editDataTableColumnDescription(description, dataTableColumnId, pf);
    }
}
