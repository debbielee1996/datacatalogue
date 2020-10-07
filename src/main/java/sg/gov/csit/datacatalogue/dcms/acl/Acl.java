package sg.gov.csit.datacatalogue.dcms.acl;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Acl {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long aclId;

    @OneToOne(mappedBy = "acl",fetch = FetchType.LAZY)
    @JsonBackReference
    private Officer officer;

    @Enumerated(EnumType.STRING)
    private AclRoleEnum aclRoleEnum;

    public Acl(Officer officer, String aclValue) {
        this.officer=officer;
        if (aclValue.equals("Public")) {
            this.aclRoleEnum=AclRoleEnum.PUBLIC;
        } else {
            this.aclRoleEnum=AclRoleEnum.SYSTEM_ADMIN;
        }
    }
}
