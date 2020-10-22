package sg.gov.csit.datacatalogue.dcms.datatablecolumn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/datatablecolumn")
public class DataTableColumnController {
    @Autowired
    DataTableColumnService dataTableColumnService;

    @GetMapping("/get-all-columns-dtos/{dataTableId}")
    public List<DataTableColumnDto> getAllColumnDtos(@PathVariable("dataTableId") String dataTableId) {
        return dataTableColumnService.getAllColumnDtos(dataTableId);
    }

}
