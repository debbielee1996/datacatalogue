package sg.gov.csit.datacatalogue.dcms.acl;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.validation.constraints.NotNull;
import lombok.*;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;
import javax.persistence.*;


@AllArgsConstructor
@NoArgsConstructor
@Entity
// pls don't change to lombok's @Data. error will be: https://stackoverflow.com/questions/17445657/hibernate-onetomany-java-lang-stackoverflowerror
@Getter
@Setter
public class Acl {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long aclId;

    @OneToOne(mappedBy = "acl",fetch = FetchType.LAZY)
    @JsonBackReference
    @NotNull
    private Officer officer;

    @Enumerated(EnumType.STRING)
    @NotNull
    private AclRoleEnum aclRoleEnum;

    public Acl(Officer officer, String aclValue) {
        this.officer=officer;

        switch(aclValue) {
            case "Public":
                this.aclRoleEnum=AclRoleEnum.PUBLIC;
                break;
            case "System Admin":
                this.aclRoleEnum=AclRoleEnum.SYSTEM_ADMIN;
                break;
        }
    }
}
