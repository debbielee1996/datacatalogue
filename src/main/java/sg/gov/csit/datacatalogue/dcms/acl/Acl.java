package sg.gov.csit.datacatalogue.dcms.acl;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sg.gov.csit.datacatalogue.dcms.datasetaccess.DatasetAccessTypeEnum;
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
    @NotNull
    private Long aclId;

    @OneToOne(mappedBy = "acl",fetch = FetchType.LAZY)
    @JsonBackReference
    @NotNull
    private Officer officer;

    @Enumerated(EnumType.STRING)
    @NotNull
    private AclRoleEnum aclRoleEnum;

    public Acl(@NotNull Officer officer, @NotNull String aclValue) {
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
