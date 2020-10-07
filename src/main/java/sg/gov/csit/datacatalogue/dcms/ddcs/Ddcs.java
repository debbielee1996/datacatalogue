package sg.gov.csit.datacatalogue.dcms.ddcs;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
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
    private int id;

    private String directorate;
    private String department;
    private String cluster;
    private String section;

    @ManyToMany(mappedBy = "ddcsList", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Officer> officerList;

    public Ddcs(String directorate, String department, String cluster, String section) {
        this.directorate=directorate;
        this.department=department;
        this.cluster=cluster;
        this.section=section;
        this.officerList=new ArrayList<>();
    }
}
