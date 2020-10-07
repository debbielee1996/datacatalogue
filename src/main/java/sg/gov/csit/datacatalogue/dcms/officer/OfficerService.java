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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OfficerService {
    @Autowired
    private final OfficerRepository officerRepository;
    @Autowired
    private final DatasetService datasetService;

    public boolean ValidateOfficerDatasetAccess(String pf, long datasetId) {
        if(IsOfficerInDatabase(pf)){
            if(datasetService.IsDatasetInDatabase(datasetId)){ // if dataset exists
                // checks whether the officer is granted access based on his Ddcs/Acl
                // dataset will already exist since prev check is done
                Optional<Dataset> dataset = datasetService.getDataset(datasetId);
                List<DatasetAccess> datasetAccessList = dataset.get().getDatasetAccessList();

                boolean officerHasAccess = false; // will only change to true if officer's ddcs or acl is found in datasetAccessList
                Optional<Officer> officer = getOfficer(pf);
                List<Ddcs> ddcsList = officer.get().getDdcsList();
                for (DatasetAccess da:datasetAccessList) {
                    // DatasetAccessService check
                    // check if value is officer or ddcs
                    if (da.getTypeInString().equals("Pf") & da.getValue().equals(pf)) { // if value is 'pf' check pf = pf (this officer's)
                        officerHasAccess = true;
                    } else if (da.getTypeInString().equals("Ddcs") & ddcsList.stream().anyMatch(s -> Integer.toString(s.getId()).equals(da.getValue()))) { // if value is 'Ddcs' check if value exists in Ddcs
                        officerHasAccess = true;
                    }
                }
                if (!officerHasAccess) {
                    throw new DatasetAccessNotFoundException(pf, datasetId);
                }
            }else{
                throw new DatasetNotFoundException(datasetId);
            }
        }else{
            throw new OfficerNotFoundException(pf);
        }
        return true;
    }

    public Optional<Officer> getOfficer(String pf) {
        return officerRepository.findById(pf);
    }

    public boolean IsOfficerInDatabase(String pf){
        return officerRepository.findById(pf).isPresent();
    }
}
