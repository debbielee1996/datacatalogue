package sg.gov.csit.datacatalogue.dcms.datasetaccess;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DatasetAccess {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "datasetId")
    @JsonManagedReference
    private Dataset dataset;

    @Enumerated(EnumType.STRING)
    private DatasetTypeEnum type;
    private String value;

    public DatasetAccess(Dataset dataset, String daType, String daValue) {
        this.dataset=dataset;
        this.value=daValue;

        if (daType.equals("Ddcs")) {
            this.type=DatasetTypeEnum.Ddcs;
        } else {
            this.type=DatasetTypeEnum.Pf;
        }
    }

    public String getTypeInString() {
        return type.getValue();
    }
}
