package sg.gov.csit.datacatalogue.dcms.officer;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class OfficerService {
    @Autowired
    private final OfficerRepository officerRepository;

    public Optional<Officer> getOfficer(String pf) {
        return officerRepository.findById(pf);
    }

    public boolean IsOfficerInDatabase(String pf){
        return officerRepository.findById(pf).isPresent();
    }

}
