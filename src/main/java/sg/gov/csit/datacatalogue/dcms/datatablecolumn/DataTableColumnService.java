package sg.gov.csit.datacatalogue.dcms.datatablecolumn;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DataTableColumnService {
    @Autowired
    DataTableColumnRepository dataTableColumnRepository;

    @Autowired
    ModelMapper modelMapper;

    public void addDataTableColumn(String name, String description, String type, DataTable dataTable) {
        dataTableColumnRepository.save(new DataTableColumn(name, description, type, dataTable));
    }

    public List<DataTableColumnDto> getAllColumnDtos(String dataTableId) {
        List<DataTableColumn> dataTableColumnList = dataTableColumnRepository.findByDataTableId(Long.parseLong(dataTableId));
        return dataTableColumnList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public DataTableColumnDto convertToDto(DataTableColumn dataTableColumn) {
        return modelMapper.map(dataTableColumn, DataTableColumnDto.class);
    }
}
