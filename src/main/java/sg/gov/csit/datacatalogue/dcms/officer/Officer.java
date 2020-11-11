package sg.gov.csit.datacatalogue.dcms.officer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import sg.gov.csit.datacatalogue.dcms.acl.Acl;
import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
// pls don't change to lombok's @Data. error will be: https://stackoverflow.com/questions/17445657/hibernate-onetomany-java-lang-stackoverflowerror
@Getter
@Setter
public class Officer {
    @Id
    @NotBlank
    private String pf;

    @NotBlank
    private String name;

    @NotBlank
    private String email;

    @NotBlank
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

    @NotNull
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "officer_dataset_custodians",
            joinColumns = @JoinColumn(name = "pf"),
            inverseJoinColumns = @JoinColumn(name= "datasetId"))
    @JsonManagedReference
    private Set<Dataset> datasetCustodianList; // Hashset because of this https://thorben-janssen.com/best-practices-for-many-to-many-associations-with-hibernate-and-jpa/

    public Officer(String pf, String name, String email, String password, String aclValue) {
        this.pf=pf;
        this.name=name;
        this.email=email;
        this.password=password;
        this.acl=new Acl(this, aclValue);
        this.datasetList=new ArrayList<>();
        this.dataTableList=new ArrayList<>();
        this.datasetCustodianList=new HashSet<Dataset>();
    }

    public void addDatasetCustodian(Dataset dataset) {
        this.datasetCustodianList.add(dataset);
    }
}
