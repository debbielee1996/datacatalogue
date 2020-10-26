package sg.gov.csit.datacatalogue.dcms.dataset;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.gov.csit.datacatalogue.dcms.databaselink.DatabaseActions;
import sg.gov.csit.datacatalogue.dcms.datasetaccess.DatasetAccess;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetAccessNotFoundException;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetExistsException;
import sg.gov.csit.datacatalogue.dcms.exception.DatasetNotFoundException;
import sg.gov.csit.datacatalogue.dcms.exception.OfficerNotFoundException;
import sg.gov.csit.datacatalogue.dcms.officer.Officer;
import sg.gov.csit.datacatalogue.dcms.officer.OfficerService;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Service
public class DatasetService {
    @Autowired
    private DatasetRepository datasetRepository;

    @Autowired
    private OfficerService officerService;

    @Autowired
    private ModelMapper modelMapper;

    public Optional<Dataset> getDatasetById(long datasetId) {
        return datasetRepository.findById(datasetId);
    }

    public boolean createNewDataset(@NotNull String name, String description, String pf) {
        if (datasetRepository.findByName(name) == null) { // if dataset hasn't exist yet
            Optional<Officer> officer = officerService.getOfficer(pf);
            if (officer.isEmpty()) {
                throw new OfficerNotFoundException(pf);
            }

            Dataset dataset = new Dataset(name, description, officer.get());
            DatasetAccess datasetAccess = new DatasetAccess(dataset, "Pf", pf); // add access for creator of dataset
            dataset.getDatasetAccessList().add(datasetAccess);

            datasetRepository.save(dataset);
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
            Optional<Dataset> dataset = datasetRepository.findById(datasetId);
            if(dataset.isPresent()){ // if dataset exists
                List<DatasetAccess> datasetAccessList = dataset.get().getDatasetAccessList();
                return officerHasAccessForDatasetGiven(pf, datasetAccessList);
            }else{
                throw new DatasetNotFoundException(datasetId);
            }
        }else{
            throw new OfficerNotFoundException(pf);
        }
    }

    public boolean officerHasAccessForDatasetGiven(String pf, List<DatasetAccess> datasetAccessList) {
        for (DatasetAccess da:datasetAccessList) {
            // DatasetAccessService check
            // check if value is officer("Pf")
            if (da.getTypeInString().equals("Pf") & da.getValue().equals(pf)) { // if value is 'pf' check pf = pf (this officer's)
                return true;
            }
        }
        return false;
    }

    public List<DatasetDto> getDatasetsCreatedByOfficer(String pf) {
        return datasetRepository.findByOfficerPf(pf).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DatasetDto> getAllDatasetDtos(String pf) {
        List<Dataset> datasets = datasetRepository.findAll();
        List<Dataset> filteredDatasets = datasets.stream()
                .filter(d -> ValidateOfficerDatasetAccess(pf, d.getId()))
                .collect(Collectors.toList());

        return filteredDatasets.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // converts Dataset to DatasetDto
    private DatasetDto convertToDto(Dataset dataset) {
        DatasetDto datasetDto = modelMapper.map(dataset, DatasetDto.class);
        return datasetDto;
    }

    public boolean deleteDataset(String datasetId) {
        datasetRepository.deleteById(Long.parseLong(datasetId));
        return true;
    }
}
