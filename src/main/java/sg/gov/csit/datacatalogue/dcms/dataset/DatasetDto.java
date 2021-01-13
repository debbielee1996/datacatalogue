package sg.gov.csit.datacatalogue.dcms.dataset;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
public class DatasetDto implements Serializable {
    private Long id;
    private String name;
    private String description;
    private String officerPf;
    private Boolean isPublic;
}
