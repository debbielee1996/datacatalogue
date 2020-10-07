package sg.gov.csit.datacatalogue.dcms.ddcs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DdcsRepository extends JpaRepository<Ddcs,Integer>{
}
