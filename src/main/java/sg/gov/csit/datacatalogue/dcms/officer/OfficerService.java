package sg.gov.csit.datacatalogue.dcms.officer;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;
import sg.gov.csit.datacatalogue.dcms.dataset.DatasetService;
import sg.gov.csit.datacatalogue.dcms.datasetaccess.DatasetAccess;
import sg.gov.csit.datacatalogue.dcms.ddcs.Ddcs;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetAccessNotFoundException;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetNotFoundException;
import sg.gov.csit.datacatalogue.dcms.exception.OfficerNotFoundException;

import java.util.List;
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
