package sg.gov.csit.datacatalogue.dcms.datasetaccess;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatasetAccessRepository extends JpaRepository<DatasetAccess,Long> {
}
