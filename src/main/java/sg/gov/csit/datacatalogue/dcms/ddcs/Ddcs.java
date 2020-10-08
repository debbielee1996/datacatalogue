package sg.gov.csit.datacatalogue.dcms.ddcs;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import javax.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Ddcs {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @NotNull
    private int id;

    @NotNull
    private String directorate;

    @NotNull
    private String department;

    @NotNull
    private String cluster;

    @NotNull
    private String section;

    @NotNull
    @ManyToMany(mappedBy = "ddcsList", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Officer> officerList;

    public Ddcs(@NotNull String directorate, @NotNull String department, @NotNull String cluster, @NotNull String section) {
        this.directorate=directorate;
        this.department=department;
        this.cluster=cluster;
        this.section=section;
        this.officerList=new ArrayList<>();
    }
}
