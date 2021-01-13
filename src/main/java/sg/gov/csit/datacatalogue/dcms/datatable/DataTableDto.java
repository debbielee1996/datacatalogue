package sg.gov.csit.datacatalogue.dcms.datatable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sg.gov.csit.datacatalogue.dcms.datatablecolumn.DataTableColumn;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class DataTableDto {
    private Long id;
    private String name;
    private String description;
    private String officerPf;
    private Long datasetId;
    private String datasetName;
    private String datasetDescription;
    private Boolean isPublic;
}
