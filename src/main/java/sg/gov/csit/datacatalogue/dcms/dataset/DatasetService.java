package sg.gov.csit.datacatalogue.dcms.dataset;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.gov.csit.datacatalogue.dcms.databaselink.DatabaseActions;
import sg.gov.csit.datacatalogue.dcms.datasetaccess.DatasetAccess;
import sg.gov.csit.datacatalogue.dcms.datatable.DataTable;
import sg.gov.csit.datacatalogue.dcms.ddcs.Ddcs;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetAccessNotFoundException;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetExistsException;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetNotFoundException;
import sg.gov.csit.datacatalogue.dcms.exception.OfficerNotFoundException;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;
import sg.gov.csit.datacatalogue.dcms.officer.OfficerService;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class DatasetService {
    @Autowired
    private DatasetRepository datasetRepository;

    @Autowired
    private OfficerService officerService;


    public boolean IsDatasetInDatabase(long id){
        return datasetRepository.findById(id).isPresent();
    }

    public Optional<Dataset> getDatasetById(long datasetId) {
        return datasetRepository.findById(datasetId);
    }

    public boolean createNewDataset(@NotNull String name, String description, String pf) {
        if (datasetRepository.findByName(name) == null) { // if dataset hasn't exist yet
            Optional<Officer> officer = officerService.getOfficer(pf);
            if (officer.isEmpty()) {
                throw new OfficerNotFoundException(pf);
            }

            datasetRepository.save(new Dataset(name, description, officer.get()));
            DatabaseActions databaseActions = new DatabaseActions();
            try {
                boolean hasCreatedDataset = databaseActions.createDatabase(name);
                return hasCreatedDataset; // will get here if its true
            } catch (Exception e) {
                System.out.println(e);
                return false; // this means that dataset doesn't exist but the db for this dataset is alr created which is not possible unless you run the test multiple times
            }
        } else {
            throw new DatasetExistsException(name);
        }
    }

    public boolean ValidateOfficerDatasetAccess(String pf, long datasetId) {
        if(officerService.IsOfficerInDatabase(pf)){
            if(IsDatasetInDatabase(datasetId)){ // if dataset exists
                // dataset and officer will already exist since prev check is done
                Optional<Dataset> dataset = getDatasetById(datasetId);
                List<DatasetAccess> datasetAccessList = dataset.get().getDatasetAccessList();
                Optional<Officer> officer = officerService.getOfficer(pf);
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

    public List<Dataset> getAllDatasets() { return datasetRepository.findAll(); }

    public List<DataTable> getDataTablesOfDataset(String datasetId) {
        return datasetRepository.findById(Long.parseLong(datasetId)).get().getDataTableList();
    }

    public List<String> getAllDatasetNames() { return datasetRepository.findAllDatasetName(); }

    public List<Dataset> getDatasetsCreatedByOfficer(String pf) {
        return datasetRepository.findByOfficerPf(pf);
    }
}
