package sg.gov.csit.datacatalogue.dcms.datatablecolumn;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;
import sg.gov.csit.datacatalogue.dcms.datatablecolumnaccess.DataTableColumnAccess;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DataTableColumn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private DataTableColumnTypeEnum type;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dataTableId")
    @JsonBackReference
    private DataTable dataTable;

    @OneToMany(mappedBy = "dataTableColumn", fetch = FetchType.LAZY,  orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DataTableColumnAccess> dataTableColumnAccessList;

    public DataTableColumn(@NotNull String name, @NotNull String description, @NotNull String dtcType, @NotNull DataTable dataTable) {
        this.name=name;
        this.description=description;
        this.dataTable=dataTable;
        this.dataTableColumnAccessList=new ArrayList<>();

        switch(dtcType) {
            case "Number":
                this.type=DataTableColumnTypeEnum.Number;
                break;
            case "Text":
                this.type=DataTableColumnTypeEnum.Text;
                break;
            case "Date":
                this.type=DataTableColumnTypeEnum.Date;
                break;
        }
    }
}
