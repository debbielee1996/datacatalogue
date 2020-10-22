package sg.gov.csit.datacatalogue.dcms.datatablecolumn;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataTableColumnRepository extends JpaRepository<DataTableColumn, Long> {
    List<DataTableColumn> findByDataTableId(Long dataTableId);
}
