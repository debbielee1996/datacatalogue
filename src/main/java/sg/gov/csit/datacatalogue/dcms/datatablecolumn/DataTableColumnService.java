package sg.gov.csit.datacatalogue.dcms.datatablecolumn;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.gov.csit.datacatalogue.dcms.datasetaccess.DatasetAccess;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;
import sg.gov.csit.datacatalogue.dcms.datatablecolumnaccess.DataTableColumnAccess;
import sg.gov.csit.datacatalogue.dcms.exception.DataTableColumnNotFoundException;
import sg.gov.csit.datacatalogue.dcms.exception.OfficerNotFoundException;
import sg.gov.csit.datacatalogue.dcms.officer.OfficerService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DataTableColumnService {
    @Autowired
    DataTableColumnRepository dataTableColumnRepository;

    @Autowired
    OfficerService officerService;

    @Autowired
    ModelMapper modelMapper;

    public void addDataTableColumn(String name, String description, String type, DataTable dataTable) {
        dataTableColumnRepository.save(new DataTableColumn(name, description, type, dataTable));
    }

    public List<DataTableColumnDto> getAllColumnDtos(String pf, String dataTableId) {
        List<DataTableColumn> dataTableColumnList = dataTableColumnRepository.findByDataTableId(Long.parseLong(dataTableId));
        List<DataTableColumn> filteredDataTableColumnList = dataTableColumnList.stream()
                                                            .filter(d -> ValidateOfficerDataTableColumnAccess(pf, d.getId()))
                                                            .collect(Collectors.toList());

        return filteredDataTableColumnList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public DataTableColumnDto convertToDto(DataTableColumn dataTableColumn) {
        return modelMapper.map(dataTableColumn, DataTableColumnDto.class);
    }

    public boolean ValidateOfficerDataTableColumnAccess(String pf, long dataTableColumnId) {
        if(officerService.IsOfficerInDatabase(pf)) {
            Optional<DataTableColumn> dataTableColumn = dataTableColumnRepository.findById(dataTableColumnId);
            if(dataTableColumn.isPresent()) {
                List<DataTableColumnAccess> dataTableColumnAccessList = dataTableColumn.get().getDataTableColumnAccessList();
                return officerHasAccessForDataTableColumn(pf, dataTableColumnAccessList);
            } else {
                throw new DataTableColumnNotFoundException(dataTableColumnId);
            }
        } else {
            throw new OfficerNotFoundException(pf);
        }
    }

    private boolean officerHasAccessForDataTableColumn(String pf, List<DataTableColumnAccess> dataTableColumnAccessList) {
        for (DataTableColumnAccess dtca:dataTableColumnAccessList) {
            // DataTableColumnAccess check
            // check if value is officer("Pf")
            if (dtca.getTypeInString().equals("Pf") & dtca.getValue().equals(pf)) { // if value is 'pf' check pf = pf (this officer's)
                return true;
            }
        }
        return false;
    }
}
