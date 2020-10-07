package sg.gov.csit.datacatalogue.dcms.officer;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sg.gov.csit.datacatalogue.dcms.acl.Acl;
import sg.gov.csit.datacatalogue.dcms.ddcs.Ddcs;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Officer {
    @Id
    private String pf;
    private String name;
    private String email;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "officer_ddcs",
            joinColumns = @JoinColumn(name = "pf"),
            inverseJoinColumns = @JoinColumn(name= "ddcsId"))
    @JsonManagedReference
    private List<Ddcs> ddcsList;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "aclId", referencedColumnName = "aclId")
    @JsonManagedReference
    private Acl acl;

    public Officer(String pf, String name, String email, String aclValue) {
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
