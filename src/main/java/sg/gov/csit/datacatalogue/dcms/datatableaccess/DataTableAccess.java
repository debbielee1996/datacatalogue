package sg.gov.csit.datacatalogue.dcms.datatableaccess;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;

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
    @JoinColumn(name = "dataTableId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private DataTable dataTable;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DataTableAccessTypeEnum type;

    private String value;

    public DataTableAccess(@NotNull DataTable dataTable, @NotNull String daType, @NotNull String daValue) {
        this.dataTable=dataTable;
        if (daType.equals("Pf")) {
            this.type=DataTableAccessTypeEnum.Pf;
        }
        this.value=daValue;
    }
}
