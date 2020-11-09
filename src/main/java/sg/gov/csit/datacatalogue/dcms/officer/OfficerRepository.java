package sg.gov.csit.datacatalogue.dcms.officer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfficerRepository extends JpaRepository<Officer,String>{
    Optional<Officer> findByPf(String pf);
}
