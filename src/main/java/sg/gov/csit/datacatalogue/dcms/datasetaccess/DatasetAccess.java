package sg.gov.csit.datacatalogue.dcms.datasetaccess;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;
import sg.gov.csit.datacatalogue.dcms.datatableaccess.DataTableAccessTypeEnum;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatasetAccess {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "datasetId")
    @JsonBackReference
    private Dataset dataset;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DatasetAccessTypeEnum type;

    private String value;

    public DatasetAccess(@NotNull Dataset dataset, @NotNull String daType, @NotNull String daValue) {
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
