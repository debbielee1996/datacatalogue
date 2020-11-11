package sg.gov.csit.datacatalogue.dcms.datasetaccess;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatasetAccess {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @NotNull
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "datasetId")
    @JsonBackReference
    private Dataset dataset;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DatasetAccessTypeEnum type;

    @NotBlank
    private String value;

    public DatasetAccess(Dataset dataset, String daType, String daValue) {
        this.dataset=dataset;
        this.value=daValue;

        switch(daType) {
            case "Pf":
                this.type=DatasetAccessTypeEnum.Pf;
                break;
        }
    }

    public String getTypeInString() {
        return type.getValue();
    }
}
