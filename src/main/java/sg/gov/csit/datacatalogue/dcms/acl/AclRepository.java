package sg.gov.csit.datacatalogue.dcms.acl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AclRepository extends JpaRepository<Acl,Long> {
}
