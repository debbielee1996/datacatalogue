package sg.gov.csit.datacatalogue.dcms.officer;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sg.gov.csit.datacatalogue.dcms.acl.Acl;
import sg.gov.csit.datacatalogue.dcms.ddcs.Ddcs;

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
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "officer_ddcs",
            joinColumns = @JoinColumn(name = "pf"),
            inverseJoinColumns = @JoinColumn(name= "ddcsId"))
    @JsonManagedReference
    private List<Ddcs> ddcsList;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "aclId", referencedColumnName = "aclId")
    @JsonManagedReference
    private Acl acl;

    public Officer(@NotNull String pf, @NotNull String name, @NotNull String email, @NotNull String aclValue) {
        this.pf=pf;
        this.name=name;
        this.email=email;
        this.ddcsList=new ArrayList<>();
        this.acl=new Acl(this, aclValue);
    }

    public void addDdcs(Ddcs ddcs) {
        this.ddcsList.add(ddcs);
    }
}
