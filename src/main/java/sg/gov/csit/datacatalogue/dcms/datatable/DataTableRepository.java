package sg.gov.csit.datacatalogue.dcms.datatable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataTableRepository extends JpaRepository<DataTable, Long> {
    List<DataTable> findByDatasetId(@Param("datasetId") Long datasetId);

    DataTable findByNameAndDatasetId(@Param("tableName") String tableName, @Param("datasetId") Long datasetId);
}
