package sg.gov.csit.datacatalogue.dcms.datatable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataTableRepository extends JpaRepository<DataTable, Long> {
    DataTable findByName(@Param("tableName") String tableName);

    @Query("Select name from DataTable")
    List<String> findAllDataTableNames();
}
