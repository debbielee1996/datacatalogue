package sg.gov.csit.datacatalogue.dcms.dataset;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class DatasetDto implements Serializable {
    private Long id;
    private String name;
    private String description;
    private String officerPf;
}
