package sg.gov.csit.datacatalogue.dcms.datatablecolumn;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;

@Service
@AllArgsConstructor
public class DataTableColumnService {
    @Autowired
    DataTableColumnRepository dataTableColumnRepository;

    public void addDataTableColumn(String name, String description, String type, DataTable dataTable) {
        dataTableColumnRepository.save(new DataTableColumn(name, description, type, dataTable));
    }

}
