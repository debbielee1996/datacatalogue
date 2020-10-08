package sg.gov.csit.datacatalogue.dcms.dataset;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sg.gov.csit.datacatalogue.dcms.datasetaccess.DatasetAccess;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Dataset {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    @OneToMany(mappedBy = "dataset", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<DatasetAccess> datasetAccessList;

    @NotNull
    @OneToMany(mappedBy = "dataset", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<DataTable> dataTableList;

    public Dataset(String name, String description) {
        this.name=name;
        this.description=description;
        this.datasetAccessList=new ArrayList<>();
        this.dataTableList=new ArrayList<>();
    }
}
