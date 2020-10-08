package sg.gov.csit.datacatalogue.dcms.datatable;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sun.istack.NotNull;
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
public class DataTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    Long id;

    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "datasetId")
    @JsonManagedReference
    private Dataset dataset;

    public DataTable(String name, String description, Dataset dataset) {
         this.name=name;
         this.description=description;
         this.dataset=dataset;
     }
}
