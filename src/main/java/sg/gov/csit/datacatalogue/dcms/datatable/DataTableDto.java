package sg.gov.csit.datacatalogue.dcms.datatable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class DataTableDto {
    private Long id;
    private String name;
    private String description;
    private String officerPf;
    private String datasetName;

}
