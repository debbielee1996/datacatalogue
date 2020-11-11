package sg.gov.csit.datacatalogue.dcms.datatablecolumn;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;
import sg.gov.csit.datacatalogue.dcms.datatablecolumnaccess.DataTableColumnAccess;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
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
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull
    private DataTableColumnTypeEnum type;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dataTableId")
    @JsonBackReference
    private DataTable dataTable;

    @OneToMany(mappedBy = "dataTableColumn", fetch = FetchType.LAZY,  orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DataTableColumnAccess> dataTableColumnAccessList;

    public DataTableColumn(String name, String description, String dtcType, DataTable dataTable) {
        this.name=name;
        this.description=description;
        this.dataTable=dataTable;
        this.dataTableColumnAccessList=new ArrayList<>();

        switch(dtcType) {
            case "Whole number (0 decimal places)":
                this.type=DataTableColumnTypeEnum.Number_0dp;
                break;
            case "Number (2 decimal places)":
                this.type=DataTableColumnTypeEnum.Number_2dp;
                break;
            case "Number (5 decimal places)":
                this.type=DataTableColumnTypeEnum.Number_5dp;
                break;
            case "Date":
                this.type=DataTableColumnTypeEnum.Date;
                break;
            case "Text":
                this.type=DataTableColumnTypeEnum.Text;
                break;
        }
    }
}
