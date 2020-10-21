package sg.gov.csit.datacatalogue.dcms.datatable;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;
import sg.gov.csit.datacatalogue.dcms.datatableaccess.DataTableAccess;
import sg.gov.csit.datacatalogue.dcms.datatablecolumn.DataTableColumn;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DataTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    private String name;

    @NotNull
    private String description;

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
    @OneToMany(mappedBy = "dataTable", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<DataTableAccess> dataTableAccessList;

    @NotNull
    @OneToMany(mappedBy = "dataTable", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DataTableColumn> dataTableColumnList;

    public DataTable(@NotNull String name, String description, @NotNull Dataset dataset, @NotNull Officer officer) {
         this.name=name;
         this.description=description;
         this.dataset=dataset;
         this.officer=officer;
         this.dataTableAccessList=new ArrayList<>();
         this.dataTableColumnList=new ArrayList<>();
     }

    public void setDataTableColumnList(List<DataTableColumn> dcList) {
        if (this.dataTableColumnList==null) {
            this.dataTableColumnList=new ArrayList<>();
        }
        this.dataTableColumnList.clear();
        if(dcList!=null) {
            this.dataTableColumnList.addAll(dcList);
        }
    }
}
