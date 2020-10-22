package sg.gov.csit.datacatalogue.dcms.datatablecolumnaccess;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataTableColumnAccessRepository extends JpaRepository<DataTableColumnAccess, Long> {
}
