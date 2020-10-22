package sg.gov.csit.datacatalogue.dcms.datatablecolumn;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
public class DataTableColumnDto implements Serializable {
    private Long id;
    private String name;
    private String description;
    private String type;
    private Long dataTableId;
    private String dataTableName;
}
