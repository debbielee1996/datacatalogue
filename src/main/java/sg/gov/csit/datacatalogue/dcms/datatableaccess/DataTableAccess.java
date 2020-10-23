package sg.gov.csit.datacatalogue.dcms.datatableaccess;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;
import sg.gov.csit.datacatalogue.dcms.datatablecolumnaccess.DataTableColumnAccessTypeEnum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DataTableAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dataTableId")
    @JsonBackReference
    private DataTable dataTable;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DataTableAccessTypeEnum type;

    private String value;

    public DataTableAccess(@NotNull DataTable dataTable, @NotNull String daType, @NotNull String dtaValue) {
        this.dataTable=dataTable;
        this.value=dtaValue;

        switch(daType) {
            case "Pf":
                this.type=DataTableAccessTypeEnum.Pf;
                break;
        }
    }

    public String getTypeInString() {
        return type.getValue();
    }
}
