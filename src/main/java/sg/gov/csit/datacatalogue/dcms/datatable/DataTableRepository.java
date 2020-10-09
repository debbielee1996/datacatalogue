package sg.gov.csit.datacatalogue.dcms.datatable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DataTableRepository extends JpaRepository<DataTable, Long> {
    DataTable findByName(@Param("tableName") String tableName);
}
