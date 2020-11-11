package sg.gov.csit.datacatalogue.dcms.datatablecolumnaccess;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sg.gov.csit.datacatalogue.dcms.datatablecolumn.DataTableColumn;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataTableColumnAccess {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dataTableColumnId")
    @JsonBackReference
    private DataTableColumn dataTableColumn;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DataTableColumnAccessTypeEnum type;

    @NotBlank
    private String value;

    public DataTableColumnAccess(DataTableColumn dataTableColumn, String dtcaType, String dtcaValue) {
        this.dataTableColumn=dataTableColumn;
        this.value=dtcaValue;

        switch(dtcaType) {
            case "Pf":
                this.type=DataTableColumnAccessTypeEnum.Pf;
                break;
        }
    }

    public String getTypeInString() {
        return type.getValue();
    }
}
