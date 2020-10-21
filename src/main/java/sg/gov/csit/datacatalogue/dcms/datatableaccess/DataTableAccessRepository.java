package sg.gov.csit.datacatalogue.dcms.datatableaccess;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataTableAccessRepository extends JpaRepository<DataTableAccess, Long> {
}
