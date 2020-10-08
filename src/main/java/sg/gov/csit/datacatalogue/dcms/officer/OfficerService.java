package sg.gov.csit.datacatalogue.dcms.officer;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.gov.csit.datacatalogue.dcms.dataset.Dataset;
import sg.gov.csit.datacatalogue.dcms.dataset.DatasetService;
import sg.gov.csit.datacatalogue.dcms.datasetaccess.DatasetAccess;
import sg.gov.csit.datacatalogue.dcms.datasetaccess.DatasetAccessService;
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
                // dataset and officer will already exist since prev check is done
                Optional<Dataset> dataset = datasetService.getDataset(datasetId);
                List<DatasetAccess> datasetAccessList = dataset.get().getDatasetAccessList();
                Optional<Officer> officer = getOfficer(pf);
                List<Ddcs> ddcsList = officer.get().getDdcsList();

                // checks whether the officer is granted access based on his Ddcs/Acl
                boolean officerHasAccess = officerHasAccessForDatasetGiven(pf, datasetAccessList, ddcsList);
                if (!officerHasAccess) {
                    throw new DatasetAccessNotFoundException(pf, datasetId);
                }
                return officerHasAccess;
            }else{
                throw new DatasetNotFoundException(datasetId);
            }
        }else{
            throw new OfficerNotFoundException(pf);
        }
    }

    public Optional<Officer> getOfficer(String pf) {
        return officerRepository.findById(pf);
    }

    public boolean IsOfficerInDatabase(String pf){
        return officerRepository.findById(pf).isPresent();
    }

    public boolean officerHasAccessForDatasetGiven(String pf, List<DatasetAccess> datasetAccessList, List<Ddcs> ddcsList) {
        for (DatasetAccess da:datasetAccessList) {
            // DatasetAccessService check
            // check if value is officer or ddcs first
            if (da.getTypeInString().equals("Pf") & da.getValue().equals(pf)) { // if value is 'pf' check pf = pf (this officer's)
                return true;
            } else if (da.getTypeInString().equals("Ddcs") & ddcsList.stream().anyMatch(s -> Integer.toString(s.getId()).equals(da.getValue()))) { // if value is 'Ddcs' check if value exists in Ddcs
                return true;
            }
        }
        return false;
    }


}
