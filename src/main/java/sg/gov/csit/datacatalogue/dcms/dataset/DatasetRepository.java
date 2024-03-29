package sg.gov.csit.datacatalogue.dcms.dataset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Long> {
    Dataset findByName(@Param("name") String name);

    @Query("Select name from Dataset")
    List<String> findAllDatasetName();

    List<Dataset> findByOfficerPf(String pf);
}
