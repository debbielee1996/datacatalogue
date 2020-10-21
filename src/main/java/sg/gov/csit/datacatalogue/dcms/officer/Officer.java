package sg.gov.csit.datacatalogue.dcms.officer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sg.gov.csit.datacatalogue.dcms.acl.Acl;
import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Officer {
    @Id
    @NotNull
    private String pf;

    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "aclId", referencedColumnName = "aclId")
    @JsonManagedReference
    private Acl acl;

    @NotNull
    @OneToMany(mappedBy = "officer", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Dataset> datasetList;

    @NotNull
    @OneToMany(mappedBy = "officer", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<DataTable> dataTableList;

    public Officer(@NotNull String pf, @NotNull String name, @NotNull String email, @NotNull String password, @NotNull String aclValue) {
        this.pf=pf;
        this.name=name;
        this.email=email;
        this.password=password;
        this.acl=new Acl(this, aclValue);
        this.datasetList=new ArrayList<>();
        this.dataTableList=new ArrayList<>();
    }
}
