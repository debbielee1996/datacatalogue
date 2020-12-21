package sg.gov.csit.datacatalogue.dcms.datatable;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;
import sg.gov.csit.datacatalogue.dcms.datatableaccess.DataTableAccess;
import sg.gov.csit.datacatalogue.dcms.datatablecolumn.DataTableColumn;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="DataTable",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"datasetId", "name"},
                                            name="datasetId_dataTableName"
    )}
)
public class DataTable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private boolean isPublic;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "datasetId")
    @JsonBackReference
    private Dataset dataset;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officerId")
    @JsonBackReference
    private Officer officer;

    @NotNull
    @OneToMany(mappedBy = "dataTable", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DataTableAccess> dataTableAccessList;

    @NotNull
    @OneToMany(mappedBy = "dataTable", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DataTableColumn> dataTableColumnList;

    public DataTable(String name, String description, Dataset dataset, Officer officer, boolean isPublic) {
         this.name=name;
         this.description=description;
        this.isPublic=isPublic;
         this.dataset=dataset;
         this.officer=officer;
         this.dataTableAccessList=new ArrayList<>();
         this.dataTableColumnList=new ArrayList<>();
     }
}
