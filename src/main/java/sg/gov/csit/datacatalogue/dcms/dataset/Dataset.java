package sg.gov.csit.datacatalogue.dcms.dataset;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sg.gov.csit.datacatalogue.dcms.datasetaccess.DatasetAccess;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dataset {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private boolean isPublic;

    @NotNull
    @OneToMany(mappedBy = "dataset", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DatasetAccess> datasetAccessList;

    @NotNull
    @OneToMany(mappedBy = "dataset", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DataTable> dataTableList;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officerId")
    @JsonBackReference
    private Officer officer; // data owner

    @NotNull
    @ManyToMany(mappedBy = "datasetCustodianList", fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<Officer> officerCustodianList; // Hashset because of this https://thorben-janssen.com/best-practices-for-many-to-many-associations-with-hibernate-and-jpa/

    public Dataset(String name, String description, Officer officer, boolean isPublic) {
        this.name=name;
        this.description=description;
        this.isPublic=isPublic;
        this.officer=officer;
        this.datasetAccessList=new ArrayList<>();
        this.dataTableList=new ArrayList<>();
        this.officerCustodianList=new HashSet<Officer>();
    }
}
